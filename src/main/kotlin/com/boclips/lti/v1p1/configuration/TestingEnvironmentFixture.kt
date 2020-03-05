package com.boclips.lti.v1p1.configuration

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("testing")
class TestingEnvironmentFixture(
    private val ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository,
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository,
    @Value("\${fixtures.ltiOnePointOne.consumerKey}") private val testingConsumerKey: String,
    @Value("\${fixtures.ltiOnePointOne.consumerSecret}") private val testingConsumerSecret: String,
    @Value("\${fixtures.videosClient.id}") private val testingClientId: String,
    @Value("\${fixtures.videosClient.secret}") private val testingClientSecret: String
) {
    @PostConstruct
    fun insertTestingConsumerFixture() {
        ltiOnePointOneConsumerRepository.insert(
            LtiOnePointOneConsumerDocument(
                id = ObjectId(),
                key = testingConsumerKey,
                secret = testingConsumerSecret
            )
        )
        integrationDocumentRepository.insert(
            IntegrationDocument(
                id = ObjectId(),
                integrationId = testingConsumerKey,
                clientId = testingClientId,
                clientSecret = testingClientSecret
            )
        )
    }
}
