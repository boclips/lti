package com.boclips.lti.v1p1.infrastructure.service

import com.boclips.lti.v1p1.configuration.properties.LtiProperties
import com.boclips.lti.v1p1.infrastructure.model.exception.ClientNotFoundException
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VideosClientFactoryTest {
    private lateinit var videosClient: VideosClient
    private lateinit var ltiProperties: LtiProperties
    private lateinit var factory: VideosClientFactory

    @BeforeEach
    fun setupVideosClientFactory() {
        videosClient = VideosClientFake()
        ltiProperties = LtiProperties().apply {
            consumer.key = "pho"
            consumer.secret = "a secret ingredient"
        }

        factory = VideosClientFactory(videosClient, ltiProperties)
    }

    @Test
    fun `returns the VideosClient for Pearson Realize matching consumer key`() {
        val returnedVideosClient = factory.getClient(ltiProperties.consumer.key)

        assertThat(returnedVideosClient).isEqualTo(videosClient)
    }

    @Test
    fun `throws an exception when the consumer key does not match`() {
        assertThrows<ClientNotFoundException> { factory.getClient("ramen") }
    }
}
