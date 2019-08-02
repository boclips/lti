package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.CollectionId
import com.boclips.videos.service.client.internal.FakeClient
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.net.URI

@ExtendWith(MockitoExtension::class)
@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class ApiCollectionRepositoryTest : AbstractSpringIntegrationTest() {
    @Test
    fun `get does not retry 404 errors and throws a ResourceNotFoundException`(@Mock collection: Collection) {
        whenever(videoServiceClient.getDetailed(collectionId))
            .thenThrow(HttpClientErrorException(HttpStatus.NOT_FOUND))
            .thenReturn(collection)

        assertThatThrownBy { collectionRepository.get(collectionIdString) }
            .isInstanceOf(ResourceNotFoundException::class.java)
            .hasMessageContaining(collectionIdString)
    }

    @Test
    fun `get rethrows other HttpClientErrorException instances`() {
        whenever(videoServiceClient.getDetailed(collectionId)).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

        assertThatThrownBy { collectionRepository.get(collectionIdString) }
            .isInstanceOf(HttpClientErrorException::class.java)
            .extracting("statusCode")
            .containsOnly(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `get retries non-404 errors up to 3 times and returns requested collection`(@Mock collection: Collection) {
        whenever(videoServiceClient.getDetailed(collectionId))
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .thenThrow(RuntimeException("Something's gone completely wrong"))
            .thenReturn(collection)

        assertThat(collectionRepository.get(collectionIdString)).isEqualTo(collection)
    }

    @Test
    fun `getMyCollections returns an empty list if no collections are found for specified owner`() {
        whenever(videoServiceClient.collectionsDetailed)
            .thenReturn(emptyList())

        assertThat(collectionRepository.getMyCollections()).isEmpty()
    }

    @Test
    fun `getMyCollections returns collections returned by video service client`(
        @Mock firstCollection: Collection,
        @Mock secondCollection: Collection
    ) {
        whenever(videoServiceClient.collectionsDetailed)
            .thenReturn(listOf(firstCollection, secondCollection))

        assertThat(collectionRepository.getMyCollections()).containsExactlyInAnyOrder(
            firstCollection,
            secondCollection
        )
    }

    @Test
    fun `getMyCollections rethrows other HttpClientErrorException instances`() {
        whenever(videoServiceClient.collectionsDetailed)
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

        assertThatThrownBy { collectionRepository.getMyCollections() }
            .isInstanceOf(HttpClientErrorException::class.java)
            .extracting("statusCode")
            .containsOnly(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `getMyCollections retries non-404 errors up to 3 times and returns found collections`(@Mock collection: Collection) {
        whenever(videoServiceClient.collectionsDetailed)
            .thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .thenThrow(RuntimeException("Something's gone completely wrong"))
            .thenReturn(listOf(collection))

        assertThat(collectionRepository.getMyCollections()).containsOnly(collection)
    }

    private final val collectionIdString = "87064254edd642a8a4c2e22a"
    private val collectionId = CollectionId(URI("https://video-service.com/collections/$collectionIdString"))

    @MockBean(name = "videoServiceClient")
    override lateinit var videoServiceClient: FakeClient

    @BeforeEach
    fun setup() {
        whenever(videoServiceClient.rawIdToCollectionId(collectionIdString)).thenReturn(collectionId)
    }
}
