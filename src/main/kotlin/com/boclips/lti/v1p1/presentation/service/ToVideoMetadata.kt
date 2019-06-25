package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.presentation.model.VideoMetadata
import com.boclips.videos.service.client.Video
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ToVideoMetadata(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory,
    private val formatDuration: FormatDuration
) {
    companion object : KLogging()

    operator fun invoke(video: Video): VideoMetadata {
        return VideoMetadata(
            uriComponentsBuilderFactory.getInstance()
                .replacePath("/v1p1/videos/${video.videoId.value}")
                .toUriString(),
            video.videoId.uri.toString(),
            video.title,
            video.description,
            video.playback.thumbnailUrl,
            formatDuration(video.playback.duration)
        )
    }
}
