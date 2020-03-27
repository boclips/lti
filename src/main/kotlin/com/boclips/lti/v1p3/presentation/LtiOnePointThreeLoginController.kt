package com.boclips.lti.v1p3.presentation

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p3.application.service.JwksKeyProvider
import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.domain.exception.InvalidSignatureException
import com.boclips.lti.v1p3.domain.exception.ResourceDoesNotMatchException
import com.boclips.lti.v1p3.domain.model.SessionKeys
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.domain.service.VerifyCrossSiteRequestForgeryProtection
import mu.KLogging
import org.hibernate.validator.constraints.URL
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Controller
@RequestMapping("/v1p3")
class LtiOnePointThreeLoginController(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory,
    private val platformRepository: PlatformRepository,
    private val verifyCrossSiteRequestForgeryProtection: VerifyCrossSiteRequestForgeryProtection,
    private val ltiSession: LtiOnePointThreeSession
) {
    companion object : KLogging()

    @PostMapping("/initiate-login")
    fun initiateLogin(
        @NotNull(message = "'iss' parameter must not be blank")
        @URL(protocol = "https", message = "'iss' parameter must be a valid HTTPS URL")
        @RequestParam("iss")
        issuer: String?,
        @NotBlank(message = "'login_hint' parameter must not be blank")
        @RequestParam("login_hint")
        loginHint: String?,
        @NotNull(message = "'target_link_uri' parameter must not be blank")
        @URL(message = "'target_link_uri' must be a valid URL")
        @RequestParam("target_link_uri")
        targetLinkUri: String?,
        @RequestParam("lti_message_hint")
        ltiMessageHint: String?,
        session: HttpSession
    ): String {
        logger.info { "LTI 1.3 Initiate Login { iss: '$issuer', login_hint: '$loginHint', target_link_uri: '$targetLinkUri' }" }

        val platform = platformRepository.getByIssuer(java.net.URL(issuer!!))

        val state = UUID.randomUUID().toString()
        session.setAttribute(SessionKeys.state, state)
        session.setAttribute(SessionKeys.targetLinkUri, targetLinkUri)

        val authenticationRequestUri = UriComponentsBuilder.fromUri(platform.authenticationEndpoint.toURI())
            .queryParam("scope", "openid")
            .queryParam("response_type", "id_token")
            .queryParam("client_id", "boclips")
            .queryParam(
                "redirect_uri",
                uriComponentsBuilderFactory.getInstance().replacePath("/v1p3/authentication-response").toUriString()
            )
            .queryParam("login_hint", loginHint!!)
            .also {
                if (!ltiMessageHint.isNullOrBlank()) it.queryParam("lti_message_hint", ltiMessageHint)
            }
            .queryParam("state", state)
            .queryParam("response_mode", "form_post")
            .queryParam("nonce", UUID.randomUUID().toString())
            .queryParam("prompt", "none")
            .toUriString()

        return "redirect:$authenticationRequestUri"
    }

    @PostMapping("/authentication-response")
    fun handleAuthenticationResponse(
        @NotBlank(message = "'state' parameter must not be blank")
        state: String?,
        @NotBlank(message = "'id_token' parameter must not be blank")
        @RequestParam(name = "id_token")
        idToken: String?
    ): String {
        verifyCrossSiteRequestForgeryProtection(state!!, ltiSession)

        val decodedToken = JWT.decode(idToken)
        val platform = platformRepository.getByIssuer(java.net.URL(decodedToken.issuer))

        val keyProvider = JwksKeyProvider(UrlJwkProvider(platform.jwksEndpoint))
        val algorithm = Algorithm.RSA256(keyProvider)

        try {
            algorithm.verify(decodedToken)
        } catch (e: SignatureVerificationException) {
            throw InvalidSignatureException()
        }

        val targetLinkUri =
            decodedToken.getClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri").asString()!!

        if (ltiSession.getTargetLinkUri() != targetLinkUri) throw ResourceDoesNotMatchException()

        logger.info { "LTI 1.3 Authentication Response Token ${decodedToken.token}" }

        ltiSession.setIntegrationId(decodedToken.issuer)

        return "redirect:${ltiSession.getTargetLinkUri()}"
    }
}
