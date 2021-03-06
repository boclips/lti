package com.boclips.lti.core.configuration.testing

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("testing")
class TestingEnvironmentFixturesCore(
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository,
    @Value("\${fixtures.ltiOnePointOne.consumerKey}") private val testingConsumerKey: String,
    @Value("\${fixtures.ltiOnePointThree.issuer}") private val issuer: String,
    @Value("\${fixtures.apiClient.id}") private val testingClientId: String,
    @Value("\${fixtures.apiClient.secret}") private val testingClientSecret: String
) {
    @PostConstruct
    fun insertIntegrationFixture() {
        integrationDocumentRepository.findOneByIntegrationId(issuer) ?: run {
            integrationDocumentRepository.insert(
                IntegrationDocument(
                    id = ObjectId(),
                    integrationId = testingConsumerKey,
                    clientId = testingClientId,
                    clientSecret = testingClientSecret
                )
            )
        }

        integrationDocumentRepository.findOneByIntegrationId(issuer) ?: run {
            integrationDocumentRepository.insert(
                IntegrationDocument(
                    id = ObjectId(),
                    integrationId = issuer,
                    clientId = testingClientId,
                    clientSecret = testingClientSecret
                )
            )
        }
    }
}
