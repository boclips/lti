package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory

class MongoBackedVideosClientFactory(
    private val videoServiceProperties: VideoServiceProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository
) : VideosClientFactory {
    override fun getClient(integrationId: String): VideosClient {
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
