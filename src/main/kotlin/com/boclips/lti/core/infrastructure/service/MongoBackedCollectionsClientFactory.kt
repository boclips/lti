package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.videos.api.httpclient.helper.ServiceAccountTokenFactory

class MongoBackedCollectionsClientFactory(
    private val boclipsApiProperties: BoclipsApiProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository
) : CollectionsClientFactory {
    override fun getClient(integrationId: String): CollectionsClient {
        val integration = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integration != null) {
            return CollectionsClient.create(
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
