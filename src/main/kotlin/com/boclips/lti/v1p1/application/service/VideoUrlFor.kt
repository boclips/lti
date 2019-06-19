package com.boclips.lti.v1p1.application.service

import com.boclips.videos.service.client.VideoServiceClient
import org.springframework.stereotype.Service

@Service
class VideoUrlFor(val videoServiceClient: VideoServiceClient) {
    operator fun invoke(videoId: String): String {
        return videoServiceClient.rawIdToVideoId(videoId).uri.toString()
    }
}
