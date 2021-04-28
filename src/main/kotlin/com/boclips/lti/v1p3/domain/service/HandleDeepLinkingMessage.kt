package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import java.net.URL

class HandleDeepLinkingMessage(
    private val resourceLinkService: ResourceLinkService
) {
    operator fun invoke(message: DeepLinkingMessage): URL {

        return resourceLinkService.getDeepLinkingLinkWithUrlQuery(message)
    }
}
