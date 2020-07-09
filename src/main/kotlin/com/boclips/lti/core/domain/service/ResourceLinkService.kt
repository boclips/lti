package com.boclips.lti.core.domain.service

import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import java.net.URL

interface ResourceLinkService {
    fun getVideoLink(video: Video): URL
    fun getCollectionLink(collection: Collection): URL
    fun getCollectionsLink(): URL
    fun getDeepLinkingLink(message: DeepLinkingMessage? = null): URL
    fun getAccessTokenLink(): URL
    fun getOnePointThreeAuthResponseLink(): URL
}
