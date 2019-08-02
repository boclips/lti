package com.boclips.lti.v1p1.presentation.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.presentation.model.CollectionMetadata
import com.boclips.videos.service.client.Collection
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
            collection.videos.mapNotNull { it?.playback?.thumbnailUrl }
        )
    }
}
