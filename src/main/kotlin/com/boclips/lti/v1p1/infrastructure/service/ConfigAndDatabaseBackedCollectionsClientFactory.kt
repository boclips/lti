package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.lti.v1p1.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory

class ConfigAndDatabaseBackedCollectionsClientFactory(
    private val preconfiguredCollectionsClient: CollectionsClient,
    private val ltiProperties: LtiProperties,
    private val videoServiceProperties: VideoServiceProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository
) : CollectionsClientFactory {
    override fun getClient(integrationId: String): CollectionsClient {
        if (integrationId == ltiProperties.consumer.key) {
            return preconfiguredCollectionsClient
        }

        val integration = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integration != null) {
            return CollectionsClient.create(
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
