package com.boclips.lti.v1p1.presentation.model

data class CollectionMetadata(
    val title: String,
    val collectionPageUrl: String,
    val thumbnailUrls: List<String>
)
