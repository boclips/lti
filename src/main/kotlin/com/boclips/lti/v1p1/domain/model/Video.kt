package com.boclips.lti.v1p1.domain.model

import java.net.URI
import java.time.Duration

data class Video(val videoId: VideoId, val title: String, val description: String?, val playback: Playback)

data class VideoId(val value: String, val uri: URI)

data class Playback(val thumbnailUrl: String, val duration: Duration)
