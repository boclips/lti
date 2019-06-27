package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoServiceClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.client.HttpClientErrorException

@Repository
class ApiVideoRepository(private val videoServiceClient: VideoServiceClient) : VideoRepository {
    override fun get(videoId: String): Video {
        val videoIdUri = videoServiceClient.rawIdToVideoId(videoId)
        try {
            return videoServiceClient.get(videoIdUri)
        }
        catch(e: HttpClientErrorException) {
            e.statusCode.let {
                if (it == HttpStatus.NOT_FOUND) {
                    throw ResourceNotFoundException(videoIdUri.uri.toString())
                } else {
                    throw e
                }
            }
        }
    }
}
