package com.boclips.lti.testsupport.factories

import com.boclips.lti.core.domain.model.Playback
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.model.VideoId
import java.net.URI
import java.time.Duration
import java.util.UUID

object VideoFactory {
    fun sample(
        videoId: String = UUID.randomUUID().toString(),
        title: String = "Superb video",
        description: String? = "You should totally see it!",
        thumbnailUrl: String = "http://server.net/thumbs/up.jpg",
        playbackDuration: Duration = Duration.ofMinutes(3)
    ) =
        Video(
            videoId = VideoId(
                value = videoId,
                uri = URI("http://server.net/v1/videos/$videoId")
            ),
            title = title,
            description = description,
            playback = Playback(
                thumbnailUrl = thumbnailUrl,
                duration = playbackDuration
            )
        )
}
