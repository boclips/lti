package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.infrastructure.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory

class MongoBackedVideosClientFactory(
    private val boclipsApiProperties: BoclipsApiProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository
) : VideosClientFactory {
    override fun getClient(integrationId: String): VideosClient {
        val integration = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integration != null) {
            return VideosClient.create(
                apiUrl = boclipsApiProperties.baseUrl,
                tokenFactory = ServiceAccountTokenFactory(
                    ServiceAccountCredentials(
                        // TODO Use token URI?
                        boclipsApiProperties.baseUrl,
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
