package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.infrastructure.exception.ResourceConversionException
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.videos.api.response.HateoasLink
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.hateoas.Link
import java.net.URI

class VideoResourceConverterTest {
    @Test
    fun `converts given resource to a domain object`() {
        val resource = VideoResourcesFactory.sampleVideo()

        val domainObject = VideoResourceConverter.toVideo(resource)

        assertThat(domainObject.videoId.value).isEqualTo(resource.id)
        assertThat(domainObject.videoId.uri).isEqualTo(URI(resource._links!!["self"]?.href!!))
        assertThat(domainObject.title).isEqualTo(resource.title)
        assertThat(domainObject.description).isEqualTo(resource.description)
        assertThat(domainObject.playback.thumbnailUrl).isNotBlank()
        assertThat(domainObject.playback.duration).isEqualTo(resource.playback!!.duration)
    }

    @Test
    fun `converts a templated thumbnail URL using 500 as default width`() {
        val resource = VideoResourcesFactory.sampleVideo(
            playback = VideoResourcesFactory.sampleStreamPlayback(
                _links = mapOf(
                    "thumbnail" to HateoasLink.of(Link.of("https://api.boclips.com/thumbnail/1_e72xmbcb/width/{thumbnailWidth}/slice/1"))
                )
            )
        )

        val domainObject = VideoResourceConverter.toVideo(resource)

        assertThat(domainObject.playback.thumbnailUrl).isEqualTo("https://api.boclips.com/thumbnail/1_e72xmbcb/width/500/slice/1")
    }

    @Test
    fun `converts a non-templated thumbnail URL`() {
        val thumbnailUrl = "https://api.boclips.com/thumbnail/1.png"
        val resource = VideoResourcesFactory.sampleVideo(
            playback = VideoResourcesFactory.sampleStreamPlayback(
                _links = mapOf(
                    "thumbnail" to HateoasLink.of(Link.of(thumbnailUrl))
                )
            )
        )

        val domainObject = VideoResourceConverter.toVideo(resource)

        assertThat(domainObject.playback.thumbnailUrl).isEqualTo(thumbnailUrl)
    }

    @Test
    fun `raises an error when thumbnail link is not available`() {
        val resource = VideoResourcesFactory.sampleVideo(
            playback = VideoResourcesFactory.sampleStreamPlayback(
                _links = emptyMap()
            )
        )

        assertThatThrownBy { VideoResourceConverter.toVideo(resource) }
            .isInstanceOf(ResourceConversionException::class.java)
            .hasMessageContaining("Thumbnail")
    }
}
