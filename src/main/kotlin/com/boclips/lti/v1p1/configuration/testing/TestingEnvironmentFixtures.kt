package com.boclips.lti.v1p1.configuration.testing

import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("testing")
class TestingEnvironmentFixtures(
    private val ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository,
    @Value("\${fixtures.ltiOnePointOne.consumerKey}") private val testingConsumerKey: String,
    @Value("\${fixtures.ltiOnePointOne.consumerSecret}") private val testingConsumerSecret: String
) {
    @PostConstruct
    fun insertConsumerFixture() {
        ltiOnePointOneConsumerRepository.insert(
            LtiOnePointOneConsumerDocument(
                id = ObjectId(),
                key = testingConsumerKey,
                secret = testingConsumerSecret
            )
        )
    }
}
