package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.model.VideoRequest
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import feign.FeignException

class ApiVideoRepository(private val videosClientFactory: VideosClientFactory) :
    VideoRepository {
    override fun get(videoRequest: VideoRequest): Video {
        val videosClient = videosClientFactory.getClient(videoRequest.integrationId)

        try {
            return VideoResourceConverter.toVideo(
                videosClient.getVideo(videoRequest.videoId)
            )
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Video with id ${videoRequest.videoId} not found")
        }
    }
}
