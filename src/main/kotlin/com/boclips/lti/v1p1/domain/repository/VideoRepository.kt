package com.boclips.lti.v1p1.domain.repository

import com.boclips.lti.v1p1.domain.model.Video

interface VideoRepository {
    fun get(videoId: String): Video
}
