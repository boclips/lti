package com.boclips.lti.core.presentation.model

data class CollectionMetadata(
    val title: String,
    val collectionPageUrl: String,
    val videosCountLabel: String,
    val thumbnailUrls: List<String?>
)
