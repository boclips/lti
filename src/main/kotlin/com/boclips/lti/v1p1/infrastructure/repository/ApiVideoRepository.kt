package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.model.Video
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.api.httpclient.VideosClient
import feign.FeignException
import org.springframework.stereotype.Repository

@Repository
class ApiVideoRepository(private val videosClient: VideosClient) : VideoRepository {
    override fun get(videoId: String): Video {
        try {
            return VideoResourceConverter.toVideo(videosClient.getVideo(videoId))
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Video with id $videoId not found")
        }
    }
}
