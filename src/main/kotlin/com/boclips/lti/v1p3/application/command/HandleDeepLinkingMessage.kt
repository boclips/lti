package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p3.application.model.setIntegrationId
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL
import javax.servlet.http.HttpSession

class HandleDeepLinkingMessage(
    private val platformRepository: PlatformRepository,
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory
) {
    operator fun invoke(message: DeepLinkingMessage, session: HttpSession): URL {
        val platform = platformRepository.getByIssuer(message.issuer)
        session.setIntegrationId(platform.issuer.toString())

        return URL(uriComponentsBuilderFactory.getInstance().replacePath("/search-and-embed").toUriString())
    }
}
