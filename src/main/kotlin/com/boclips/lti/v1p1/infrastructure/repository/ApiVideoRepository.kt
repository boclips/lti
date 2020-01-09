package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.api.httpclient.VideosClient
import com.boclips.videos.api.response.video.VideoResource
import mu.KLogging
import org.springframework.stereotype.Repository

@Repository
class ApiVideoRepository(private val videosClient: VideosClient) : VideoRepository {
    companion object : KLogging()

    override fun get(videoId: String): VideoResource {
        return videosClient.getVideo(videoId)
    }
}
