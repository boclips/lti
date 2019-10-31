package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId
import com.boclips.videos.service.client.internal.FakeClient
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.net.URI

@ExtendWith(MockitoExtension::class)
@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
private class ApiVideoRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `rethrows other HttpClientErrorException instances`() {
        whenever(videoServiceClient.get(videoId)).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

        assertThatThrownBy { videoRepository.get(videoIdString) }
            .isInstanceOf(HttpClientErrorException::class.java)
            .extracting("statusCode")
            .isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `retries non-404 errors up to 3 times and returns requested video`(@Mock video: Video) {
        whenever(videoServiceClient.get(videoId))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .thenThrow(RuntimeException("Something's gone completely wrong"))
            .thenReturn(video)

        assertThat(videoRepository.get(videoIdString)).isEqualTo(video)
    }

    @Test
    fun `does not retry 404 errors and throws a ResourceNotFoundException`(@Mock video: Video) {
        whenever(videoServiceClient.get(videoId))
            .thenThrow(HttpClientErrorException(HttpStatus.NOT_FOUND))
            .thenReturn(video)

        assertThatThrownBy { videoRepository.get(videoIdString) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining(videoIdString)
    }

    private val videoIdString = "87064254edd642a8a4c2e22a"
    private val videoId = VideoId(URI("https://video-service.com/videos/$videoIdString"))

    @MockBean(name = "videoServiceClient")
    override lateinit var videoServiceClient: FakeClient

    @BeforeEach
    fun setup() {
        whenever(videoServiceClient.rawIdToVideoId(videoIdString)).thenReturn(videoId)
    }
}
