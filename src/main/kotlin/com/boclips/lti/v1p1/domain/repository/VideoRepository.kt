package com.boclips.lti.v1p1.domain.repository

import com.boclips.videos.api.response.video.VideoResource

interface VideoRepository {
    fun get(videoId: String): VideoResource
}
