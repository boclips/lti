package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.VideoServiceClient
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Repository
import org.springframework.web.client.HttpClientErrorException

@Repository
class ApiCollectionRepository(
    private val videoServiceClient: VideoServiceClient
) : CollectionRepository {
    companion object : KLogging()

    @Retryable(
        maxAttempts = 3,
        exclude = [ResourceNotFoundException::class],
        backoff = Backoff(
            multiplier = 1.5
        )
    )
    override fun get(collectionId: String): Collection {
        val collectionIdUri = videoServiceClient.rawIdToCollectionId(collectionId)
        try {
            return videoServiceClient.getDetailed(collectionIdUri)
        } catch (e: HttpClientErrorException) {
            e.statusCode.let {
                if (it == HttpStatus.NOT_FOUND) {
                    throw ResourceNotFoundException(collectionIdUri.uri.toString())
                } else {
                    throw e
                }
            }
        }
    }

    @Recover
    fun getRecoveryMethod(e: Exception): Collection {
        logger.warn { "Error retrieving a collection from video service: $e" }
        throw e
    }

    @Retryable(
        maxAttempts = 3,
        backoff = Backoff(
            multiplier = 1.5
        )
    )
    override fun getMyCollections(): List<Collection> {
        return videoServiceClient.collectionsDetailed
    }

    @Recover
    fun getMyCollectionsRecoveryMethod(e: Exception): List<Collection> {
        logger.warn { "Error retrieving a my collections from video service: $e" }
        throw e
    }
}
