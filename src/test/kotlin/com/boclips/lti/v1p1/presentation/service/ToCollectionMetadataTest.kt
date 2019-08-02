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
    fun `returns metadata corresponding to provided collection`() {
        val collectionMetadata = toCollectionMetadata(collection)

        assertThat(collectionMetadata.title).isEqualTo(collection.title)
        assertThat(collectionMetadata.collectionPageUrl).isEqualTo("http://localhost/v1p1/collections/$collectionId")
        assertThat(collectionMetadata.thumbnailUrls).containsExactly(firstVideoThumbnailUrl, secondVideoThumbnailUrl)
    }

    private val firstVideoThumbnailUrl = "http://thumbnails.com/first"
    private val firstVideo = Video.builder()
        .playback(
            Playback.builder()
                .thumbnailUrl(firstVideoThumbnailUrl)
                .build()
        )
        .build()
    private val secondVideoThumbnailUrl = "http://thumbnails.com/second"
    private val secondVideo = Video.builder()
        .playback(
            Playback.builder()
                .thumbnailUrl(secondVideoThumbnailUrl)
                .build()
        )
        .build()
    private val thirdVideo = Video.builder().build()

    private val collectionId = 123
    private val collection: Collection = Collection.builder()
        .collectionId(CollectionId(URI("http://localhost/v1/collections/$collectionId")))
        .title("Superb collection!")
        .videos(listOf(firstVideo, secondVideo, thirdVideo))
        .subjects(emptySet())
        .build()

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
