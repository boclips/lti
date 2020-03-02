package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.videos.api.httpclient.CollectionsClient

class CollectionsClientFactory(
    private val collectionsClient: CollectionsClient,
    private val ltiProperties: LtiProperties
) {
    fun getClient(integrationId: String): CollectionsClient {
        if (integrationId == ltiProperties.consumer.key) {
            return collectionsClient
        } else {
            throw ClientNotFoundException(integrationId)
        }
    }
}
