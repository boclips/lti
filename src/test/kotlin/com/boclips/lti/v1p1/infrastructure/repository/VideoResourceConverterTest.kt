package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.testsupport.factories.VideoResourceFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class VideoResourceConverterTest {
    @Test
    fun `converts given resource to a domain object`() {
        val resource = VideoResourceFactory.sample()

        val domainObject = VideoResourceConverter.toVideo(resource)

        assertThat(domainObject.videoId.value).isEqualTo(resource.id)
        assertThat(domainObject.videoId.uri).isEqualTo(URI(resource._links!!["self"]?.href!!))
        assertThat(domainObject.title).isEqualTo(resource.title)
        assertThat(domainObject.description).isEqualTo(resource.description)
        assertThat(domainObject.playback.thumbnailUrl).isEqualTo(resource.playback!!.thumbnailUrl)
        assertThat(domainObject.playback.duration).isEqualTo(resource.playback!!.duration)
    }
}
