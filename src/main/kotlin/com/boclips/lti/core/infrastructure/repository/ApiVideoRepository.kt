package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.model.VideoQuery
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.infrastructure.service.VideosClientFactory
import feign.FeignException

class ApiVideoRepository(private val videosClientFactory: VideosClientFactory) :
    VideoRepository {
    override fun get(videoQuery: VideoQuery): Video {
        val videosClient = videosClientFactory.getClient(videoQuery.integrationId)

        try {
            return VideoResourceConverter.toVideo(
                videosClient.getVideo(videoQuery.videoId)
            )
        } catch (exception: FeignException.NotFound) {
            throw ResourceNotFoundException("Video with id ${videoQuery.videoId} not found")
        }
    }
}
