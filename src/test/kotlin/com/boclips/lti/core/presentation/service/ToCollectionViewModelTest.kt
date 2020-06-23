package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.infrastructure.service.UriComponentsBuilderFactory
import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.infrastructure.service.SpringRequestAwareResourceLinkService
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
class ToCollectionViewModelTest {
    @Test
    fun `returns metadata corresponding to provided collection and limits thumbnails to 4 items`() {
        val collectionId = "123"
        val collection: Collection =
            CollectionFactory.sample(collectionId = collectionId, title = "Superb collection!", videos = videos)

        val collectionMetadata = toCollectionViewModel(collection)

        assertThat(collectionMetadata.title).isEqualTo(collection.title)
        assertThat(collectionMetadata.collectionPageUrl).isEqualTo("http://localhost/collections/$collectionId")
        assertThat(collectionMetadata.videosCountLabel).isEqualTo("9 videos")
        assertThat(collectionMetadata.thumbnailUrls).containsExactly(
            videos.component1().playback.thumbnailUrl,
            videos.component2().playback.thumbnailUrl,
            videos.component3().playback.thumbnailUrl,
            videos.component4().playback.thumbnailUrl
        )
    }

    @Test
    fun `fills thumbnails array with nulls when video count is below 4`() {
        val collectionWithTwoVideos: Collection = CollectionFactory.sample(
            collectionId = "only-two",
            title = "Superb collection with only 2 videos!",
            videos = videos.subList(0, 2)
        )

        val collectionMetadata = toCollectionViewModel(collectionWithTwoVideos)

        assertThat(collectionMetadata.videosCountLabel).isEqualTo("2 videos")
        assertThat(collectionMetadata.thumbnailUrls).containsExactly(
            videos.component1().playback.thumbnailUrl,
            videos.component2().playback.thumbnailUrl,
            null,
            null
        )
    }

    @Test
    fun `uses singular form when there is only one video`() {
        val collectionWithTwoVideos: Collection = CollectionFactory.sample(
            collectionId = "only-one",
            title = "Superb collection with only 1 video!",
            videos = listOf(videos.component1())
        )

        val collectionMetadata = toCollectionViewModel(collectionWithTwoVideos)

        assertThat(collectionMetadata.videosCountLabel).isEqualTo("1 video")
    }

    private val videos = 1.until(10).map { VideoFactory.sample(thumbnailUrl = "http://thumbnails.com/$it") }

    private lateinit var toCollectionViewModel: ToCollectionViewModel

    @BeforeEach
    private fun setup(
        @Mock uriComponentsBuilderFactory: UriComponentsBuilderFactory
    ) {
        toCollectionViewModel =
            ToCollectionViewModel(
                SpringRequestAwareResourceLinkService(uriComponentsBuilderFactory)
            )

        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/collections")
        )
    }
}
