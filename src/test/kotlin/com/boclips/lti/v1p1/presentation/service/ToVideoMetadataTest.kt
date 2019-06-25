package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.videos.service.client.Playback
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration.ofMinutes

@ExtendWith(MockitoExtension::class)
class ToVideoMetadataTest {
    @Test
    fun `returns metadata corresponding to returned video`() {
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/v1p1/collections/e608ca4427514e4d9f5f14d4")
        )
        val video = Video.builder()
            .videoId(videoId)
            .title("Test video")
            .description("Elaborate description")
            .playback(
                Playback.builder()
                    .duration(ofMinutes(5))
                    .thumbnailUrl("")
                    .build()
            )
            .build()

        val metadata = toVideoMetadata(video)

        assertThat(metadata).isNotNull
        assertThat(metadata.videoPageUrl).isEqualTo("http://localhost/v1p1/videos/$videoIdString")
        assertThat(metadata.playbackUrl).isEqualTo(videoId.uri.toString())
        assertThat(metadata.title).isEqualTo(video.title)
        assertThat(metadata.description).isEqualTo(video.description)
        assertThat(metadata.duration).isEqualTo("5m")
        assertThat(metadata.thumbnailUrl).isEqualTo(video.playback.thumbnailUrl)
    }

    private val videoIdString = "87064254edd642a8a4c2e22a"
    private val videoId = VideoId(URI("https://video-service.com/videos/$videoIdString"))

    private lateinit var uriComponentsBuilderFactory: UriComponentsBuilderFactory
    private lateinit var toVideoMetadata: ToVideoMetadata

    @BeforeEach
    private fun setup(
        @Mock uriComponentsBuilderFactory: UriComponentsBuilderFactory
    ) {
        this.uriComponentsBuilderFactory = uriComponentsBuilderFactory
        toVideoMetadata =
            ToVideoMetadata(uriComponentsBuilderFactory, FormatDuration())
    }
}
