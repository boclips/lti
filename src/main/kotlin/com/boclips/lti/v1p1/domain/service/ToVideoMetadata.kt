package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.domain.model.VideoMetadata
import com.boclips.videos.service.client.Video
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ToVideoMetadata(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory
) {
    companion object : KLogging()

    operator fun invoke(video: Video): VideoMetadata? {
        return if (video.playback != null) {
            VideoMetadata(
                uriComponentsBuilderFactory.getInstance()
                    .replacePath("/v1p1/videos/${video.videoId.value}")
                    .toUriString(),
                video.title,
                video.description,
                video.playback.thumbnailUrl,
                video.playback.duration
            )
        } else {
            logger.warn { "Playback not available for Video ${video.videoId}" }
            null
        }
    }
}
