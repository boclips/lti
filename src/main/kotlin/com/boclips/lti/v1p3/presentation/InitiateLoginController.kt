package com.boclips.lti.v1p3.presentation

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p3.application.model.mapStateToTargetLinkUri
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import mu.KLogging
import org.hibernate.validator.constraints.URL
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID
import javax.servlet.http.HttpSession
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@Controller
class InitiateLoginController(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory,
    private val platformRepository: PlatformRepository
) {
    companion object : KLogging()

    @PostMapping("/v1p3/initiate-login")
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
        val platform = platformRepository.getByIssuer(java.net.URL(issuer!!))

        val state = UUID.randomUUID().toString()
        session.mapStateToTargetLinkUri(state, targetLinkUri!!)

        logger.info { "LTI 1.3 Initiate Login { iss: '$issuer', login_hint: '$loginHint', target_link_uri: '$targetLinkUri' }, state: '$state'" }

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
}
