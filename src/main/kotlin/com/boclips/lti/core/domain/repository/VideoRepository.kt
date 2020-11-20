package com.boclips.lti.core.domain.repository

import com.boclips.lti.core.domain.model.VideoQuery
import com.boclips.lti.core.domain.model.Video

interface VideoRepository {
    fun get(videoQuery: VideoQuery): Video
}
