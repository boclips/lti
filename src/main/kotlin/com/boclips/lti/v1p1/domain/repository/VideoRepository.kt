package com.boclips.lti.v1p1.domain.repository

import com.boclips.lti.v1p1.domain.model.Video
import com.boclips.lti.core.domain.model.VideoRequest

interface VideoRepository {
    fun get(videoRequest: VideoRequest): Video
}
