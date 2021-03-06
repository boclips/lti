package com.boclips.lti.core.presentation.model

data class CollectionViewModel(
    val title: String,
    val collectionPageUrl: String,
    val videosCountLabel: String,
    val thumbnailUrls: List<String?>
)
