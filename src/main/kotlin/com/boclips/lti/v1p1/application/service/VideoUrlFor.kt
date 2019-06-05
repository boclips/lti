package com.boclips.lti.v1p1.application.service

import com.boclips.lti.v1p1.configuration.properties.ApiProperties
import org.springframework.stereotype.Service

@Service
class VideoUrlFor(val apiProperties: ApiProperties) {
    operator fun invoke(videoId: String): String {
        return "${apiProperties.url}/v1/videos/$videoId"
    }
}
