package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.core.presentation.service.ToVideoViewModel.Companion.mobileDescriptionLength
import com.boclips.lti.core.presentation.service.ToVideoViewModel.Companion.shortDescriptionLength
import com.boclips.lti.testsupport.factories.VideoFactory
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class ToVideoViewModelTest {
    @Test
    fun `returns metadata corresponding to returned video`() {
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/collections/e608ca4427514e4d9f5f14d4")
        )
        val description =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris lobortis neque urna, eget viverra magna iaculis non. Proin nulla dui, tempor eget dignissim in, mollis quis tortor. Phasellus sit amet suscipit mi, nec tincidunt purus. Nullam ligula ante, molestie non tristique eu, laoreet in enim. Vivamus vitae placerat arcu. Donec."
        val video = VideoFactory.sample(
            videoId = videoIdString,
            title = "Test video",
            description = description,
            thumbnailUrl = "https://playbacks.com/playback/123",
            playbackDuration = Duration.ofMinutes(5)
        )

        val metadata = toVideoViewModel(video)

        assertThat(metadata).isNotNull
        assertThat(metadata.videoPageUrl).isEqualTo("http://localhost/videos/$videoIdString")
        assertThat(metadata.playbackUrl).isEqualTo(video.videoId.uri.toString())
        assertThat(metadata.playerAuthUrl).isEqualTo("http://localhost/auth/token")
        assertThat(metadata.title).isEqualTo(video.title)
        assertThat(metadata.description).isEqualTo(video.description)
        assertThat(metadata.shortDescription).isEqualTo(
            "${video.description!!.substring(
                0,
                shortDescriptionLength
            )}..."
        )
        assertThat(metadata.mobileDescription).isEqualTo(
            "${video.description!!.substring(
                0,
                mobileDescriptionLength
            )}..."
        )
        assertThat(metadata.duration).isEqualTo("5m")
        assertThat(metadata.thumbnailUrl).isEqualTo(video.playback.thumbnailUrl)
    }

    @Test
    fun `returns full description as short description if it's less than 300 chars`() {
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/collections/e608ca4427514e4d9f5f14d4")
        )
        val video = VideoFactory.sample(
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris lobortis neque urna, eget viverra magna iaculis non. Proin nulla dui, tempor eget dignissim in, mollis quis tortor. Phasellus sit amet suscipit mi, nec tincidunt purus."
        )

        val metadata = toVideoViewModel(video)

        assertThat(metadata.shortDescription).isEqualTo(video.description)
    }

    @Test
    fun `returns full description as mobile description if it's less than 100 chars`() {
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/collections/e608ca4427514e4d9f5f14d4")
        )
        val video = VideoFactory.sample(
            description = "Just a short description"
        )

        val metadata = toVideoViewModel(video)

        assertThat(metadata.mobileDescription).isEqualTo(video.description)
    }

    @Test
    fun `returns blank strings if description is null`() {
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/collections/e608ca4427514e4d9f5f14d4")
        )
        val video = VideoFactory.sample(
            description = null
        )

        val metadata = toVideoViewModel(video)

        assertThat(metadata.description).isEqualTo("")
        assertThat(metadata.shortDescription).isEqualTo("")
        assertThat(metadata.mobileDescription).isEqualTo("")
    }

    private val videoIdString = "87064254edd642a8a4c2e22a"

    private lateinit var uriComponentsBuilderFactory: UriComponentsBuilderFactory
    private lateinit var toVideoViewModel: ToVideoViewModel

    @BeforeEach
    private fun setup(
        @Mock uriComponentsBuilderFactory: UriComponentsBuilderFactory
    ) {
        this.uriComponentsBuilderFactory = uriComponentsBuilderFactory
        toVideoViewModel =
            ToVideoViewModel(
                uriComponentsBuilderFactory,
                FormatDuration()
            )
    }
}
