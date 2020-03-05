package com.boclips.lti.core.infrastructure.service

import com.boclips.videos.api.httpclient.VideosClient

interface VideosClientFactory {
    fun getClient(integrationId: String): VideosClient
}
