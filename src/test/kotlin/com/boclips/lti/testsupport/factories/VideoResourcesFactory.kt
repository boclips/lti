package com.boclips.lti.testsupport.factories

import com.boclips.videos.api.request.video.PlaybackResource
import com.boclips.videos.api.request.video.StreamPlaybackResource
import com.boclips.videos.api.response.HateoasLink
import com.boclips.videos.api.response.video.VideoResource
import org.springframework.hateoas.Link
import java.time.Duration
import java.util.UUID

object VideoResourcesFactory {
    fun sampleVideo(
        videoId: String = UUID.randomUUID().toString(),
        title: String = "A title",
        description: String = "A description",
        playback: PlaybackResource = sampleStreamPlayback(),
        contentPartnerId: String = UUID.randomUUID().toString(),
        contentPartnerVideoId: String = UUID.randomUUID().toString(),
        apiBaseUrl: String = "http://server.net"
    ) = VideoResource(
        id = videoId,
        title = title,
        description = description,
        playback = playback,
        channelId = contentPartnerId,
        channelVideoId = contentPartnerVideoId,
        _links = mapOf("self" to HateoasLink.of(Link.of("$apiBaseUrl/v1/videos/$videoId")))
    )

    fun sampleStreamPlayback(
        duration: Duration = Duration.ofMinutes(3),
        _links: Map<String, HateoasLink> = mapOf("thumbnail" to HateoasLink.of(Link.of("http://thumbs.up/1/width/{thumbnailWidth}/2.png")))
    ): PlaybackResource {
        return StreamPlaybackResource(
            id = UUID.randomUUID().toString(),
            duration = duration,
            downloadUrl = "http://download.com/2",
            referenceId = UUID.randomUUID().toString(),
            _links = _links
        )
    }
}
