package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p3.application.model.mapStateToTargetLinkUri
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.util.UUID
import javax.servlet.http.HttpSession

class AssembleLoginRequestUrl(
    private val platformRepository: PlatformRepository,
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory
) {
    operator fun invoke(
        issuer: String,
        loginHint: String,
        targetLinkUri: String,
        ltiMessageHint: String? = null,
        session: HttpSession
    ): URL {
        val platform = platformRepository.getByIssuer(URL(issuer))

        val state = UUID.randomUUID().toString()
        session.mapStateToTargetLinkUri(state, targetLinkUri)

        val authenticationRequestUri = UriComponentsBuilder.fromUri(platform.authenticationEndpoint.toURI())
            .queryParam("scope", "openid")
            .queryParam("response_type", "id_token")
            .queryParam("client_id", platform.clientId)
            .queryParam(
                "redirect_uri",
                uriComponentsBuilderFactory.getInstance()
                    .replacePath("/v1p3/authentication-response")
                    .replaceQuery(null)
                    .toUriString()
            )
            .queryParam("login_hint", loginHint)
            .also {
                if (!ltiMessageHint.isNullOrBlank()) it.queryParam("lti_message_hint", ltiMessageHint)
            }
            .queryParam("state", state)
            .queryParam("response_mode", "form_post")
            .queryParam("nonce", UUID.randomUUID().toString())
            .queryParam("prompt", "none")
            .build()
            .toUri()

        return authenticationRequestUri.toURL()
    }
}
