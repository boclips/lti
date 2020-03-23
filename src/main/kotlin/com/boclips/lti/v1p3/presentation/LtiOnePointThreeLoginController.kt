package com.boclips.lti.v1p3.presentation

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

@Validated
@Controller
@RequestMapping("/v1p3/login")
class LtiOnePointThreeLoginController(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    @PostMapping
    fun login(
        @NotNull @URL(protocol = "https") @RequestParam("iss") issuer: String?,
        @NotBlank @RequestParam("login_hint") loginHint: String?,
        @NotNull @URL @RequestParam("target_link_uri") targetLinkUri: String?,
        session: HttpSession
    ): String {
        val state = UUID.randomUUID().toString()
        session.setAttribute(SessionKeys.state, state)

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
}
