package com.boclips.lti.core.domain.repository

import com.boclips.lti.core.domain.model.VideoRequest
import com.boclips.lti.core.domain.model.Video

interface VideoRepository {
    fun get(videoRequest: VideoRequest): Video
}
