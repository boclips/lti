package com.boclips.lti.v1p1.domain.repository

import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId

interface VideoRepository {
    fun get(videoId: VideoId): Video
}
