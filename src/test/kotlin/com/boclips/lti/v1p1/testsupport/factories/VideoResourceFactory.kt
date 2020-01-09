package com.boclips.lti.v1p1.testsupport.factories

import com.boclips.videos.api.request.video.StreamPlaybackResource
import com.boclips.videos.api.response.video.VideoResource
import org.springframework.hateoas.Link
import java.time.Duration
import java.util.UUID

object VideoResourceFactory {
    fun sample(
        videoId: String = UUID.randomUUID().toString(),
        title: String = "A title",
        description: String = "A description",
        thumbnailUrl: String = "http://thumbs.up/1/2.png",
        duration: Duration = Duration.ofMinutes(3),
        contentPartnerId: String = UUID.randomUUID().toString(),
        contentPartnerVideoId: String = UUID.randomUUID().toString(),
        apiBaseUrl: String = "http://server.net"
    ) = VideoResource(
        id = videoId,
        title = title,
        description = description,
        playback = StreamPlaybackResource(
            id = UUID.randomUUID().toString(),
            thumbnailUrl = thumbnailUrl,
            duration = duration,
            streamUrl = "http://stream.org/1",
            downloadUrl = "http://download.com/2",
            referenceId = UUID.randomUUID().toString(),
            _links = emptyMap()
        ),
        contentPartnerId = contentPartnerId,
        contentPartnerVideoId = contentPartnerVideoId,
        _links = mapOf("self" to Link("$apiBaseUrl/v1/videos/$videoId"))
    )
}
