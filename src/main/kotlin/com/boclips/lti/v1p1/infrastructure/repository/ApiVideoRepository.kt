package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId
import com.boclips.videos.service.client.VideoServiceClient
import com.boclips.videos.service.client.exceptions.VideoNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

class ApiVideoRepository(private val videoServiceClient: VideoServiceClient) : VideoRepository {
    override fun get(videoId: VideoId): Video {
        try {
            return videoServiceClient[videoId]
        }
        catch(e: HttpClientErrorException) {
            e.statusCode.let {
                if (it == HttpStatus.NOT_FOUND) {
                    throw VideoNotFoundException(videoId)
                } else {
                    throw e
                }
            }
        }
    }
}
