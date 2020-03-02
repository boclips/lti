package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.videos.api.httpclient.CollectionsClient
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CollectionsClientFactoryTest {
    private lateinit var collectionsClient: CollectionsClient
    private lateinit var ltiProperties: LtiProperties
    private lateinit var factory: CollectionsClientFactory

    @BeforeEach
    fun setupCollectionsClientFactory() {
        collectionsClient = CollectionsClientFake()
        ltiProperties = LtiProperties().apply {
            consumer.key = "pho"
            consumer.secret = "a secret ingredient"
        }

        factory = CollectionsClientFactory(collectionsClient, ltiProperties)
    }

    @Test
    fun `returns the CollectionsClient for Pearson Realize matching consumer key`() {
        val returnedCollectionsClient = factory.getClient(ltiProperties.consumer.key)

        Assertions.assertThat(returnedCollectionsClient).isEqualTo(collectionsClient)
    }

    @Test
    fun `throws an exception when the consumer key does not match`() {
        assertThrows<ClientNotFoundException> { factory.getClient("ramen") }
    }
}
