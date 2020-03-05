package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.exception.ResourceNotFoundException
import com.boclips.lti.v1p1.domain.model.Video
import com.boclips.lti.core.domain.model.VideoRequest
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.lti.v1p1.infrastructure.service.VideosClientFactory
import feign.FeignException
import org.springframework.stereotype.Repository

@Repository
class ApiVideoRepository(private val videosClientFactory: VideosClientFactory) : VideoRepository {
    override fun get(videoRequest: VideoRequest): Video {
        val videosClient = videosClientFactory.getClient(videoRequest.integrationId)

        try {
            return VideoResourceConverter.toVideo(videosClient.getVideo(videoRequest.videoId))
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Video with id ${videoRequest.videoId} not found")
        }
    }
}
