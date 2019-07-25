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
        val uriComponentsBuilder = uriComponentsBuilderFactory.getInstance()
        return VideoMetadata(
            videoPageUrl = uriComponentsBuilder
                .replacePath("/v1p1/videos/${video.videoId.value}")
                .toUriString(),
            playbackUrl = video.videoId.uri.toString(),
            playerAuthUrl = uriComponentsBuilder
                .replacePath("/auth/token")
                .toUriString(),
            title = video.title,
            description = video.description ?: "",
            shortDescription = formatShortDescription(video.description),
            thumbnailUrl = video.playback.thumbnailUrl,
            duration = formatDuration(video.playback.duration)
        )
    }

    private fun formatShortDescription(description: String?): String {
        return when {
            description == null -> ""
            description.length > 100 -> "${description.substring(0, 100).trim()}..."
            else -> description
        }
    }
}
