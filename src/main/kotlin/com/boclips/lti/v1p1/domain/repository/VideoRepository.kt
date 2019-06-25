package com.boclips.lti.v1p1.domain.repository

import com.boclips.videos.service.client.Video

interface VideoRepository {
    fun get(videoId: String): Video
}
