package com.boclips.lti.v1p3.presentation

import com.auth0.jwt.JWT
import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p3.domain.model.SessionKeys
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
import com.boclips.lti.core.application.model.SessionKeys as CoreSessionKeys

@Validated
@Controller
@RequestMapping("/v1p3")
class LtiOnePointThreeLoginController(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory
) {
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
        session: HttpSession
    ): String {
        val state = UUID.randomUUID().toString()
        session.setAttribute(SessionKeys.state, state)
        session.setAttribute(SessionKeys.targetLinkUri, targetLinkUri)

        // TODO I believe the value of iss parameter doesn't necessarily need to match the Platform authentication endpoint.
        // We probably need to store a mapping on our side and retrieve the auth endpoint based on iss value.
        val authenticationRequestUri = UriComponentsBuilder.fromUriString(issuer!!)
            .queryParam("scope", "openid")
            .queryParam("response_type", "id_token")
            .queryParam("client_id", "boclips")
            .queryParam(
                "redirect_uri",
                uriComponentsBuilderFactory.getInstance().replacePath("/v1p3/auth").toUriString()
            )
            .queryParam("login_hint", loginHint!!)
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
        idToken: String?,
        session: HttpSession
    ): String {
        val resource = session.getAttribute(SessionKeys.targetLinkUri)

        val decodedToken = JWT.decode(idToken)

        session.setAttribute(CoreSessionKeys.integrationId, decodedToken.issuer)

        return "redirect:$resource"
    }
}
