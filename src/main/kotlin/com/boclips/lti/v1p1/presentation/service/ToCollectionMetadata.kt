package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.presentation.model.CollectionMetadata
import com.boclips.videos.api.response.collection.CollectionResource
import com.boclips.videos.api.response.video.VideoResource
import org.springframework.stereotype.Service

@Service
class ToCollectionMetadata(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    operator fun invoke(collection: CollectionResource): CollectionMetadata {
        return CollectionMetadata(
            collection.title!!,
            uriComponentsBuilderFactory.getInstance()
                .replacePath(
                    "/v1p1/collections/${collection.id}"
                )
                .toUriString(),
            getVideosCountLabel(collection.videos),
            getPreviewThumbnails(collection.videos)
        )
    }

    private fun getVideosCountLabel(videos: List<VideoResource>): String {
        return when {
            videos.size == 1 -> "1 video"
            else -> "${videos.size} videos"
        }
    }

    private fun getPreviewThumbnails(videos: List<VideoResource>): List<String?> {
        val thumbnails = videos.mapNotNull { it.playback?.thumbnailUrl }

        return when {
            thumbnails.size >= 4 -> thumbnails.subList(0, 4)
            else -> (thumbnails + listOf(null, null, null, null)).subList(0, 4)
        }
    }
}
