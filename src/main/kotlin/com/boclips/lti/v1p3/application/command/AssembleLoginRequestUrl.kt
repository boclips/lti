package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.domain.model.mapStateToTargetLinkUri
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import mu.KLogging
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL
import java.util.UUID
import javax.servlet.http.HttpSession

class AssembleLoginRequestUrl(
    private val platformRepository: PlatformRepository,
    private val resourceLinkService: ResourceLinkService
) {
    companion object : KLogging()

    operator fun invoke(
        issuer: String,
        loginHint: String,
        targetLinkUri: String,
        ltiMessageHint: String? = null,
        session: HttpSession
    ): URL? {

        logger.info { "reached here" }

            val platform = platformRepository.getByIssuer(URL("https://www.doesntexist.com"))

            val state = UUID.randomUUID().toString()
            session.mapStateToTargetLinkUri(state, targetLinkUri)

            val authenticationRequestUri = UriComponentsBuilder.fromUri(platform.authenticationEndpoint.toURI())
                .queryParam("scope", "openid")
                .queryParam("response_type", "id_token")
                .queryParam("client_id", platform.clientId)
                .queryParam("redirect_uri", resourceLinkService.getOnePointThreeAuthResponseLink().toString())
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
