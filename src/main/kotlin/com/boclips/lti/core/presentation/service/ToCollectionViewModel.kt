package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.presentation.model.CollectionViewModel

class ToCollectionViewModel(private val resourceLinkService: ResourceLinkService) {
    operator fun invoke(collection: Collection): CollectionViewModel {
        return CollectionViewModel(
            collection.title,
            resourceLinkService.getCollectionLink(collection).toString(),
            getVideosCountLabel(collection.videos),
            getPreviewThumbnails(collection.videos)
        )
    }

    private fun getVideosCountLabel(videos: List<Video>): String {
        return when (videos.size) {
            1 -> "1 video"
            else -> "${videos.size} videos"
        }
    }

    private fun getPreviewThumbnails(videos: List<Video>): List<String?> {
        val thumbnails = videos.map { it.playback.thumbnailUrl }

        return when {
            thumbnails.size >= 4 -> thumbnails.subList(0, 4)
            else -> (thumbnails + listOf(null, null, null, null)).subList(0, 4)
        }
    }
}
