package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.v1p1.infrastructure.model.exception.IntegrationNotFoundException
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class KeycloakClientFactory(val integrationDocumentRepository: MongoIntegrationDocumentRepository) {
    fun getKeycloakClient(integrationId: String): RestTemplate {
        val integrationKeyCloakClient = integrationDocumentRepository.findOneByIntegrationId(integrationId)

        if (integrationKeyCloakClient !== null) {
          return RestTemplateBuilder().basicAuthentication(
                integrationKeyCloakClient.clientId,
                integrationKeyCloakClient.clientSecret
            ).build()
        } else {
            throw IntegrationNotFoundException(integrationId)
        }
    }
}
