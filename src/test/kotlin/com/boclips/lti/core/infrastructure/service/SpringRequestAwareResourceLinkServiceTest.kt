package com.boclips.lti.core.infrastructure.service

import com.boclips.lti.core.infrastructure.model.SearchType
import com.boclips.lti.testsupport.factories.CollectionFactory
import com.boclips.lti.testsupport.factories.DeepLinkingMessageFactory
import com.boclips.lti.testsupport.factories.VideoFactory
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.util.UriComponentsBuilder
import java.net.URL

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
    fun `returns an embeddable video link`() {
        val video = VideoFactory.sample(videoId = "abc")
        val videoLink = resourceLinkService.getEmbeddableVideoLink(video)
        assertThat(videoLink.toString()).isEqualTo("http://localhost/embeddable-videos/abc")
    }

    @Nested
    inner class DeepLinking {
        @Test
        fun `returns a deep linking link with query params when given a message`() {
            val deepLinkingLink = resourceLinkService.getDeepLinkingLink(
                message = DeepLinkingMessageFactory.sample(
                    returnUrl = URL("https://returnurl.com"),
                    data = "data",
                    deploymentId = "id"
                )
            )

            assertThat(deepLinkingLink.toString()).startsWith("http://localhost/search-and-embed")
            assertThat(deepLinkingLink).hasParameter("deep_link_return_url", "https://returnurl.com")
            assertThat(deepLinkingLink).hasParameter("data", "data")
            assertThat(deepLinkingLink).hasParameter("deployment_id", "id")
        }

        @Test
        fun `does not preserve unexpected query parameters`() {
            val deepLinkingLink = resourceLinkService.getDeepLinkingLink(
                message = DeepLinkingMessageFactory.sample(data = null)
            )

            assertThat(deepLinkingLink).hasNoParameter("with")
        }

        @Test
        fun `does not add data parameter if no data is given`() {
            val deepLinkingLink = resourceLinkService.getDeepLinkingLink(
                message = DeepLinkingMessageFactory.sample(data = null)
            )

            assertThat(deepLinkingLink).hasNoParameter("data")
        }

        @Test
        fun `returns a vanilla deep link url when not given a message`() {
            val deepLinkingLink = resourceLinkService.getDeepLinkingLink()

            assertThat(deepLinkingLink.toString()).startsWith("http://localhost/search-and-embed")
        }
    }

    @Nested
    inner class SearchResponseLink {
        @Test
        fun `returns a search response url with copy link feature params set to true`() {
            val searchLink = resourceLinkService.getSearchVideoLink(showCopyLink = true, SearchType.SEARCH)
            assertThat(searchLink).hasParameter(
                "embeddable_video_url",
                "http://localhost/embeddable-videos/%7Bid%7D?with=params"
            )
            assertThat(searchLink).hasParameter("show_copy_link", "true")
        }

        @Test
        fun `returns a search response url with copy link feature params set to false`() {
            val searchLink = resourceLinkService.getSearchVideoLink(showCopyLink = false, SearchType.SEARCH)
            assertThat(searchLink).hasParameter(
                "embeddable_video_url",
                "http://localhost/embeddable-videos/%7Bid%7D?with=params"
            )
            assertThat(searchLink).hasParameter("show_copy_link", "false")
        }
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
