package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.CollectionId
import com.boclips.videos.service.client.Playback
import com.boclips.videos.service.client.Video
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@ExtendWith(MockitoExtension::class)
class ToCollectionMetadataTest {
    @Test
    fun `returns metadata corresponding to provided collection and limits thumbnails to 4 items`() {
        val collectionId = 123
        val collection: Collection = Collection.builder()
            .collectionId(CollectionId(URI("http://localhost/v1/collections/$collectionId")))
            .title("Superb collection!")
            .videos(videos)
            .subjects(emptySet())
            .build()

        val collectionMetadata = toCollectionMetadata(collection)

        assertThat(collectionMetadata.title).isEqualTo(collection.title)
        assertThat(collectionMetadata.collectionPageUrl).isEqualTo("http://localhost/v1p1/collections/$collectionId")
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
        val collectionWithTwoVideos: Collection = Collection.builder()
            .collectionId(CollectionId(URI("http://localhost/v1/collections/only-two")))
            .title("Superb collection with only 2 videos!")
            .videos(videos.subList(0, 2))
            .subjects(emptySet())
            .build()

        val collectionMetadata = toCollectionMetadata(collectionWithTwoVideos)

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
        val collectionWithTwoVideos: Collection = Collection.builder()
            .collectionId(CollectionId(URI("http://localhost/v1/collections/only-one")))
            .title("Superb collection with only 1 video!")
            .videos(listOf(videos.component1()))
            .subjects(emptySet())
            .build()

        val collectionMetadata = toCollectionMetadata(collectionWithTwoVideos)

        assertThat(collectionMetadata.videosCountLabel).isEqualTo("1 video")
    }

    private val videos = 1.until(10).map {
        Video.builder()
            .playback(
                Playback.builder()
                    .thumbnailUrl("http://thumbnails.com/$it")
                    .build()
            )
            .build()
    }

    private lateinit var uriComponentsBuilderFactory: UriComponentsBuilderFactory
    private lateinit var toCollectionMetadata: ToCollectionMetadata

    @BeforeEach
    private fun setup(
        @Mock uriComponentsBuilderFactory: UriComponentsBuilderFactory
    ) {
        this.uriComponentsBuilderFactory = uriComponentsBuilderFactory

        toCollectionMetadata =
            ToCollectionMetadata(uriComponentsBuilderFactory)

        whenever(uriComponentsBuilderFactory.getInstance()).thenReturn(
            UriComponentsBuilder.fromHttpUrl("http://localhost/v1p1/collections")
        )
    }
}
