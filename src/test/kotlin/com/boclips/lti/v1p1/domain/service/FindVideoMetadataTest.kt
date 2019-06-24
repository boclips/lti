package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.service.client.Playback
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId
import com.boclips.videos.service.client.exceptions.VideoNotFoundException
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration.ofMinutes

@ExtendWith(MockitoExtension::class)
class FindVideoMetadataTest {
  @Test
  fun `returns null if given video is not found`() {
    whenever(videoRepository.get(videoId)).thenThrow(VideoNotFoundException(videoId))

    assertThat(findVideoMetadata(videoId)).isNull()
  }

  @Test
  fun `propagates other kinds of exceptions`() {
    whenever(videoRepository.get(videoId)).thenThrow(IllegalStateException())

    assertThatThrownBy { findVideoMetadata(videoId) }.isInstanceOf(IllegalStateException::class.java)
  }

  @Test
  fun `returns metadata corresponding to returned video`() {
    val video = Video.builder()
      .title("Test video")
      .description("Elaborate description")
      .playback(
        Playback.builder()
          .duration(ofMinutes(5))
          .thumbnailUrl("")
          .build()
      )
      .build()
    whenever(videoRepository.get(videoId)).thenReturn(video)
    whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
      UriComponentsBuilder.fromHttpUrl("http://localhost/v1p1/collections/e608ca4427514e4d9f5f14d4")
    )

    val metadata = findVideoMetadata(videoId)

    assertThat(metadata).isNotNull
    assertThat(metadata?.videoUrl).isEqualTo("http://localhost/v1p1/videos/$videoIdString")
    assertThat(metadata?.title).isEqualTo(video.title)
    assertThat(metadata?.description).isEqualTo(video.description)
    assertThat(metadata?.duration).isEqualTo(video.playback.duration)
    assertThat(metadata?.thumbnailUrl).isEqualTo(video.playback.thumbnailUrl)
  }

  private val videoIdString = "87064254edd642a8a4c2e22a"
  private val videoId = VideoId(URI("https://video-service.com/videos/$videoIdString"))

  private lateinit var videoRepository: VideoRepository
  private lateinit var uriComponentsBuilderFactory: UriComponentsBuilderFactory
  private lateinit var findVideoMetadata: FindVideoMetadata

  @BeforeEach
  private fun setup(
    @Mock videoRepository: VideoRepository,
    @Mock uriComponentsBuilderFactory: UriComponentsBuilderFactory
  ) {
    this.videoRepository = videoRepository
    this.uriComponentsBuilderFactory = uriComponentsBuilderFactory
    findVideoMetadata = FindVideoMetadata(videoRepository, uriComponentsBuilderFactory)
  }
}
