package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.videos.api.httpclient.CollectionsClient

interface CollectionsClientFactory {
    fun getClient(integrationId: String): CollectionsClient
}
