package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.CollectionNotFoundException
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.VideoServiceClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.client.HttpClientErrorException

@Repository
class ApiCollectionRepository(
    private val videoServiceClient: VideoServiceClient
) : CollectionRepository {
    override fun get(collectionIdString: String): Collection {
        val collectionId = videoServiceClient.rawIdToCollectionId(collectionIdString)
        try {
            return videoServiceClient[collectionId]
        }
        catch(e: HttpClientErrorException) {
            e.statusCode.let {
                if (it == HttpStatus.NOT_FOUND) {
                    throw CollectionNotFoundException("Collection not found for id = $collectionIdString")
                } else {
                    throw e
                }
            }
        }
    }
}
