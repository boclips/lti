package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.infrastructure.exception.IntegrationNotFoundException
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

class KeycloakClientFactory(val integrationDocumentRepository: MongoIntegrationDocumentRepository) {
    fun getKeycloakClient(integrationId: String): RestTemplate {
        val integrationKeyCloakClient = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integrationKeyCloakClient !== null) {
          return RestTemplateBuilder().basicAuthentication(
                integrationKeyCloakClient.clientId,
                integrationKeyCloakClient.clientSecret
            ).build()
        } else {
            throw IntegrationNotFoundException(
                integrationId
            )
        }
    }
}
