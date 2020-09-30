package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.users.api.httpclient.IntegrationsClient
import com.boclips.users.api.httpclient.UsersClient
import com.boclips.users.api.httpclient.helper.ServiceAccountCredentials
import com.boclips.users.api.httpclient.helper.ServiceAccountTokenFactory

class MongoBackedIntegrationsClientFactory(
    private val boclipsApiProperties: BoclipsApiProperties,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository
) : IntegrationsClientFactory {
    override fun getClient(integrationId: String): IntegrationsClient {
        val integration = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        return integration?.let {
            return IntegrationsClient.create(
                apiUrl = boclipsApiProperties.baseUrl,
                tokenFactory = ServiceAccountTokenFactory(
                    ServiceAccountCredentials(
                        authEndpoint = boclipsApiProperties.baseUrl,
                        clientId = it.clientId,
                        clientSecret = it.clientSecret
                    )
                )
            )
        } ?: throw ClientNotFoundException(integrationId)
    }
}
