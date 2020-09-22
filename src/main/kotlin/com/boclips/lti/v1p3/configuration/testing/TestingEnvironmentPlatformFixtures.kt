package com.boclips.lti.v1p3.configuration.testing

import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import com.boclips.lti.v1p3.infrastructure.repository.MongoPlatformDocumentRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@Profile("testing")
class TestingEnvironmentPlatformFixtures(
    private val mongoPlatformDocumentRepository: MongoPlatformDocumentRepository,
    @Value("\${fixtures.ltiOnePointThree.issuer}") private val issuer: String,
    @Value("\${fixtures.ltiOnePointThree.authEndpoint}") private val authEndpoint: String,
    @Value("\${fixtures.ltiOnePointThree.jwksEndpoint}") private val jwksEndpoint: String,
    @Value("\${fixtures.ltiOnePointThree.clientId}") private val clientId: String
) {
    @PostConstruct
    fun insertConsumerFixture() {
        mongoPlatformDocumentRepository.findByIssuer(issuer = issuer)
            ?: run {
                mongoPlatformDocumentRepository.insert(PlatformDocument(
                    id = ObjectId(),
                    issuer = issuer,
                    authenticationEndpoint = authEndpoint,
                    jwksUrl = jwksEndpoint,
                    clientId = clientId
                ))
            }
    }
}
