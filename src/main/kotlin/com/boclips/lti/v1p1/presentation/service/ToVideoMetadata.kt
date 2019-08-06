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
    companion object : KLogging() {
        const val shortDescriptionLength = 300
        const val mobileDescriptionLength = 100
    }

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
            shortDescription = trimDescription(video.description, shortDescriptionLength),
            mobileDescription = trimDescription(video.description, mobileDescriptionLength),
            thumbnailUrl = video.playback.thumbnailUrl,
            duration = formatDuration(video.playback.duration)
        )
    }

    private fun trimDescription(description: String?, trimLength: Int): String {
        return when {
            description == null -> ""
            description.length > trimLength -> "${description.substring(0, trimLength).trim()}..."
            else -> description
        }
    }
}
