package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.testsupport.factories.CollectionFactory
import com.boclips.lti.testsupport.factories.VideoFactory
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.util.UriComponentsBuilder

@ExtendWith(MockitoExtension::class)
class SpringRequestAwareResourceLinkServiceTest {
    @Test
    fun `returns a collections link`() {
        val collectionsLink = resourceLinkService.getCollectionsLink()
        assertThat(collectionsLink.toString()).isEqualTo("http://localhost/collections")
    }

    @Test
    fun `returns a collection link`() {
        val collection = CollectionFactory.sample(collectionId = "123")
        val collectionLink = resourceLinkService.getCollectionLink(collection)
        assertThat(collectionLink.toString()).isEqualTo("http://localhost/collections/123")
    }

    @Test
    fun `returns a video link`() {
        val video = VideoFactory.sample(videoId = "abc")
        val videoLink = resourceLinkService.getVideoLink(video)
        assertThat(videoLink.toString()).isEqualTo("http://localhost/videos/abc")
    }

    @Test
    fun `returns a deep linking link`() {
        val deepLinkingLink = resourceLinkService.getDeepLinkingLink()
        assertThat(deepLinkingLink.toString()).isEqualTo("http://localhost/search-and-embed")
    }

    @Test
    fun `returns an auth token link`() {
        val accessTokenLink = resourceLinkService.getAccessTokenLink()
        assertThat(accessTokenLink.toString()).isEqualTo("http://localhost/auth/token")
    }

    @Test
    fun `returns an LTI 1 3 authentication response link`() {
        val authResponseLink = resourceLinkService.getOnePointThreeAuthResponseLink()
        assertThat(authResponseLink.toString()).isEqualTo("http://localhost/v1p3/authentication-response")
    }

    private lateinit var resourceLinkService: SpringRequestAwareResourceLinkService

    @BeforeEach
    fun setupRequestContext(@Mock uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/something?with=params")
        )
        resourceLinkService = SpringRequestAwareResourceLinkService(uriComponentsBuilderFactory)
    }
}