package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import java.net.URL

class HandleResourceLinkMessage(
    private val linkService: ResourceLinkService
) {
    operator fun invoke(message: ResourceLinkMessage): URL {
        return if (message.requestedResource.isSearchResourceRequest()) {
            linkService.getSearchVideoLink()
        } else {
            message.requestedResource
        }
    }
}

fun URL.isSearchResourceRequest() = this.toString().endsWith("search")
