package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.presentation.model.VideoViewModel
import mu.KLogging

class ToVideoViewModel(
    private val resourceLinkService: ResourceLinkService,
    private val formatDuration: FormatDuration
) {
    companion object : KLogging() {
        const val shortDescriptionLength = 300
        const val mobileDescriptionLength = 100
    }

    operator fun invoke(video: Video): VideoViewModel {
        return VideoViewModel(
            videoPageUrl = resourceLinkService.getVideoLink(video).toString(),
            playbackUrl = video.videoId.uri.toString(),
            playerAuthUrl = resourceLinkService.getAccessTokenLink().toString(),
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
