package com.boclips.lti.core.presentation.model

data class VideoMetadata (
    val videoPageUrl: String,
    val playbackUrl: String,
    val playerAuthUrl: String,
    val title: String,
    val description: String,
    val shortDescription: String,
    val mobileDescription: String,
    val thumbnailUrl: String,
    val duration: String
)
