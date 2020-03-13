package com.boclips.lti.core.presentation.service

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.core.domain.model.Collection
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.presentation.model.CollectionViewModel

class ToCollectionViewModel(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    operator fun invoke(collection: Collection): CollectionViewModel {
        return CollectionViewModel(
            collection.title,
            uriComponentsBuilderFactory.getInstance()
                .replacePath(
                    "/collections/${collection.collectionId.uri.toString().substringAfterLast("/")}"
                )
                .toUriString(),
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
