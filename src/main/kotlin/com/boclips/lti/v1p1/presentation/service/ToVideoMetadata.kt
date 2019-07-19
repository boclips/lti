package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.GetLtiGatewayProxyPath
import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.presentation.model.VideoMetadata
import com.boclips.videos.service.client.Video
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class ToVideoMetadata(
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory,
    private val formatDuration: FormatDuration,
    private val getLtiGatewayProxyPath: GetLtiGatewayProxyPath
) {
    companion object : KLogging()

    operator fun invoke(video: Video): VideoMetadata {
        val uriComponentsBuilder = uriComponentsBuilderFactory.getInstance()
        return VideoMetadata(
            uriComponentsBuilder
                .replacePath("/v1p1/videos/${video.videoId.value}")
                .toUriString(),
            uriComponentsBuilder
                .replacePath(getLtiGatewayProxyPath(video.videoId.uri.path))
                .toUriString(),
            video.title,
            video.description ?: "",
            formatShortDescription(video.description),
            video.playback.thumbnailUrl,
            formatDuration(video.playback.duration)
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
