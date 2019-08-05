package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.presentation.model.CollectionMetadata
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.Video
import org.springframework.stereotype.Service

@Service
class ToCollectionMetadata(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    operator fun invoke(collection: Collection): CollectionMetadata {
        return CollectionMetadata(
            collection.title,
            uriComponentsBuilderFactory.getInstance()
                .replacePath(
                "/v1p1/collections/${collection.collectionId.uri.toString().substringAfterLast("/")}")
                .toUriString(),
            getVideosCountLabel(collection.videos),
            getPreviewThumbnails(collection.videos)
        )
    }

    private fun getVideosCountLabel(videos: List<Video>): String {
        return when {
            videos.size == 1 -> "1 video"
            else -> "${videos.size} videos"
        }
    }

    private fun getPreviewThumbnails(videos: List<Video>): List<String?> {
        val thumbnails = videos.mapNotNull { it.playback?.thumbnailUrl }

        return when {
            thumbnails.size >= 4 -> thumbnails.subList(0, 4)
            else -> (thumbnails + listOf(null, null, null, null)).subList(0, 4)
        }
    }
}
