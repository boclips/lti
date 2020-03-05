package com.boclips.lti.core.infrastructure.service

import com.boclips.videos.api.httpclient.CollectionsClient

interface CollectionsClientFactory {
    fun getClient(integrationId: String): CollectionsClient
}
