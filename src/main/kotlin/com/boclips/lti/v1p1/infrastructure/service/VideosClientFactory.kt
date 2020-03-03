package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.videos.api.httpclient.VideosClient

interface VideosClientFactory {
    fun getClient(integrationId: String): VideosClient
}
