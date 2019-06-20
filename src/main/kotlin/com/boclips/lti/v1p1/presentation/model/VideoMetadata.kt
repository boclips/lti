package com.boclips.lti.v1p1.presentation.model

import java.time.Duration

data class VideoMetadata (
    val videoUrl: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val duration: Duration
)
