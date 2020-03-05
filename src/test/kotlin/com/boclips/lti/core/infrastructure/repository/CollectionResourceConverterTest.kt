package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.v1p1.testsupport.factories.CollectionResourceFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class CollectionResourceConverterTest {
    @Test
    fun `converts given resource into a domain object`() {
        val resource = CollectionResourceFactory.sample()

        val domainObject = CollectionResourceConverter.toCollection(resource)

        assertThat(domainObject.collectionId.uri).isEqualTo(URI(resource._links!!["self"]?.href!!))
        assertThat(domainObject.title).isEqualTo(resource.title)
        assertThat(domainObject.videos).isEqualTo(resource.videos.map(VideoResourceConverter::toVideo))
    }
}
