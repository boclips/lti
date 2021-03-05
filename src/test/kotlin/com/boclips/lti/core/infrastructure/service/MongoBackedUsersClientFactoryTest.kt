package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.exception.ClientNotFoundException
import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.testsupport.factories.JaegerTracerFactory
import com.boclips.users.api.httpclient.UsersClient
import com.boclips.users.api.httpclient.test.fakes.UsersClientFake
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MongoBackedUsersClientFactoryTest {
    private lateinit var preconfiguredUsersClient: UsersClient
    private lateinit var integrationDocumentRepository: MongoIntegrationDocumentRepository
    private lateinit var boclipsApiProperties: BoclipsApiProperties
    private lateinit var factory: MongoBackedUsersClientFactory

    @BeforeEach
    fun setupUsersClientFactory(@Mock integrationDocumentRepository: MongoIntegrationDocumentRepository) {
        preconfiguredUsersClient = UsersClientFake()
        this.integrationDocumentRepository = integrationDocumentRepository
        boclipsApiProperties = BoclipsApiProperties()
            .apply {
                baseUrl = "https://api.com/"
            }

        factory = MongoBackedUsersClientFactory(
            boclipsApiProperties = boclipsApiProperties,
            integrationDocumentRepository = integrationDocumentRepository,
            tracer = JaegerTracerFactory.createTracer()
        )
    }

    @Test
    fun `configures the client through a database`() {
        val integrationDocument = IntegrationDocument(
            id = ObjectId(),
            integrationId = "miso",
            clientId = "super",
            clientSecret = "secret"
        )
        whenever(integrationDocumentRepository.findOneByIntegrationId("miso")).thenReturn(integrationDocument)

        val returnedUsersClient = factory.getClient("miso")

        assertThat(returnedUsersClient)
            .isNotNull()
            .isNotEqualTo(preconfiguredUsersClient)
    }

    @Test
    fun `throws an exception when the consumer key does not match`() {
        assertThrows<ClientNotFoundException> { factory.getClient("ramen") }
    }
}
