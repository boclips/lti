package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.videos.api.httpclient.VideosClient

class VideosClientFactory(
    private val videosClient: VideosClient,
    private val ltiProperties: LtiProperties
) {
    fun getClient(consumerKey: String): VideosClient {
        if (consumerKey == ltiProperties.consumer.key) {
            return videosClient
        } else {
            throw ClientNotFoundException(consumerKey)
        }
    }
}
