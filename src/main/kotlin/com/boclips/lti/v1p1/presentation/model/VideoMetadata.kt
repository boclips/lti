package com.boclips.lti.v1p1.presentation.model

data class VideoMetadata (
    val videoPageUrl: String,
    val playbackUrl: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val duration: String
)
