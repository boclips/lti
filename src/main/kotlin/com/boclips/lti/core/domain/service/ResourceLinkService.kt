package com.boclips.lti.core.domain.service

import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.Video
import java.net.URL

interface ResourceLinkService {
    fun getVideoLink(video: Video): URL
    fun getCollectionLink(collection: Collection): URL
    fun getCollectionsLink(): URL
    fun getDeepLinkingLink(): URL
    fun getAccessTokenLink(): URL
    fun getOnePointThreeAuthResponseLink(): URL
}
