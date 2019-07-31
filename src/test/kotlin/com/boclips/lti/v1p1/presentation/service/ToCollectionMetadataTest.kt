package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.CollectionId
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
    }

    private val collectionId = 123
    private val collection: Collection = Collection.builder()
        .collectionId(CollectionId(URI("http://localhost/v1/collections/$collectionId")))
        .title("Superb collection!")
        .videos(emptyList())
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
