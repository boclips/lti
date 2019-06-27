package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.CollectionId
import com.boclips.videos.service.client.VideoServiceClient
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import java.net.URI

@ExtendWith(MockitoExtension::class)
class ApiCollectionRepositoryTest {
  @Test
  fun `throws CollectionNotFoundException when given collection is not found in the API`() {
    whenever(videoServiceClient.getDetailed(collectionId)).thenThrow(HttpClientErrorException(HttpStatus.NOT_FOUND))

    assertThatThrownBy { collectionRepository.get(collectionIdString) }
      .isInstanceOf(ResourceNotFoundException::class.java)
      .hasMessageContaining(collectionIdString)
  }

  @Test
  fun `rethrows other HttpClientErrorException instances`() {
    whenever(videoServiceClient.getDetailed(collectionId)).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST))

    assertThatThrownBy { collectionRepository.get(collectionIdString) }
      .isInstanceOf(HttpClientErrorException::class.java)
      .extracting("statusCode")
      .containsOnly(HttpStatus.BAD_REQUEST)
  }

  @Test
  fun `returns a requested collection`(@Mock collection: Collection) {
    whenever(videoServiceClient.getDetailed(collectionId)).thenReturn(collection)

    assertThat(collectionRepository.get(collectionIdString)).isEqualTo(collection)
  }

  private val collectionIdString = "87064254edd642a8a4c2e22a"
  private val collectionId = CollectionId(URI("https://video-service.com/collections/$collectionIdString"))

  private lateinit var videoServiceClient: VideoServiceClient
  private lateinit var collectionRepository: ApiCollectionRepository

  @BeforeEach
  fun setup(@Mock videoServiceClientMock: VideoServiceClient) {
    videoServiceClient = videoServiceClientMock
    collectionRepository = ApiCollectionRepository(videoServiceClient)

    whenever(videoServiceClient.rawIdToCollectionId(collectionIdString)).thenReturn(collectionId)
  }
}
