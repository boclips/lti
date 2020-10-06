package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.domain.model.setIntegrationId
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL
import javax.servlet.http.HttpSession

class HandleDeepLinkingMessage(
    private val resourceLinkService: ResourceLinkService
) {
    operator fun invoke(message: DeepLinkingMessage): URL {

        return resourceLinkService.getDeepLinkingLink(message)
    }
}
