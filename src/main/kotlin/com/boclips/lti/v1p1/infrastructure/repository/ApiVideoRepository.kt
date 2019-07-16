package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoServiceClient
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Repository
import org.springframework.web.client.HttpClientErrorException

@Repository
class ApiVideoRepository(private val videoServiceClient: VideoServiceClient) : VideoRepository {
    companion object : KLogging()

    @Retryable(
        maxAttempts = 3,
        exclude = [ResourceNotFoundException::class],
        backoff = Backoff(
            multiplier = 1.5
        )
    )
    override fun get(videoId: String): Video {
        val videoIdUri = videoServiceClient.rawIdToVideoId(videoId)
        try {
            return videoServiceClient.get(videoIdUri)
        } catch (e: HttpClientErrorException) {
            e.statusCode.let {
                if (it == HttpStatus.NOT_FOUND) {
                    throw ResourceNotFoundException(videoIdUri.uri.toString())
                } else {
                    throw e
                }
            }
        }
    }

    @Recover
    fun getRecoveryMethod(e: Exception): Video {
        logger.warn { "Error retrieving a video from video service: $e" }
        throw e
    }
}
