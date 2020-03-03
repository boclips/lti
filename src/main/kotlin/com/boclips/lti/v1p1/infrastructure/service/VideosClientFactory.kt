package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory

class VideosClientFactory(
    private val preconfiguredVideosClient: VideosClient,
    private val ltiProperties: LtiProperties,
    private val videoServiceProperties: VideoServiceProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository
) {
    fun getClient(integrationId: String): VideosClient {
        if (integrationId == ltiProperties.consumer.key) {
            return preconfiguredVideosClient
        }

        val integration = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integration != null) {
            return VideosClient.create(
                apiUrl = videoServiceProperties.baseUrl,
                tokenFactory = ServiceAccountTokenFactory(
                    ServiceAccountCredentials(
                        // TODO Use token URI?
                        videoServiceProperties.baseUrl,
                        integration.clientId,
                        integration.clientSecret
                    )
                )
            )
        } else {
            throw ClientNotFoundException(integrationId)
        }
    }
}
