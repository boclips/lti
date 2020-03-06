package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.presentation.model.VideoMetadata
import mu.KLogging

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
            shortDescription = trimDescription(
                video.description,
                shortDescriptionLength
            ),
            mobileDescription = trimDescription(
                video.description,
                mobileDescriptionLength
            ),
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
