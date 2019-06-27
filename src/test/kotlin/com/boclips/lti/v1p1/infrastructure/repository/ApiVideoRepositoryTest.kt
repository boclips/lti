package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId
import com.boclips.videos.service.client.VideoServiceClient
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.net.URI

@ExtendWith(MockitoExtension::class)
class ApiVideoRepositoryTest {
  @Test
  fun `throws VideoNotFoundException when given video is not found in the API`() {
    whenever(videoServiceClient.get(videoId)).thenThrow(HttpClientErrorException(HttpStatus.NOT_FOUND))

    Assertions.assertThatThrownBy { videoRepository.get(videoIdString) }
      .isInstanceOf(ResourceNotFoundException::class.java)
      .hasMessageContaining(videoIdString)
  }

  @Test
  fun `rethrows other HttpClientErrorException instances`() {
    whenever(videoServiceClient.get(videoId)).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

    Assertions.assertThatThrownBy { videoRepository.get(videoIdString) }
      .isInstanceOf(HttpClientErrorException::class.java)
      .extracting("statusCode")
      .containsOnly(HttpStatus.BAD_REQUEST)
  }

  @Test
  fun `returns a requested video`(@Mock video: Video) {
    whenever(videoServiceClient.get(videoId)).thenReturn(video)

    assertThat(videoRepository.get(videoIdString)).isEqualTo(video)
  }

  private val videoIdString = "87064254edd642a8a4c2e22a"
  private val videoId = VideoId(URI("https://video-service.com/videos/$videoIdString"))

  private lateinit var videoServiceClient: VideoServiceClient
  private lateinit var videoRepository: ApiVideoRepository

  @BeforeEach
  fun setup(@Mock videoServiceClientMock: VideoServiceClient) {
    videoServiceClient = videoServiceClientMock
    videoRepository = ApiVideoRepository(videoServiceClient)

    whenever(videoServiceClient.rawIdToVideoId(videoIdString)).thenReturn(videoId)
  }
}
