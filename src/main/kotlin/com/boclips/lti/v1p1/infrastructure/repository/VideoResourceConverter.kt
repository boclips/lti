package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.model.Playback
import com.boclips.lti.v1p1.domain.model.Video
import com.boclips.lti.v1p1.domain.model.VideoId
import com.boclips.videos.api.response.video.VideoResource
import java.net.URI

object VideoResourceConverter {
    fun toVideo(resource: VideoResource) = Video(
        videoId = VideoId(value = resource.id!!, uri = URI(resource._links!!["self"]?.href!!)),
        title = resource.title!!,
        description = resource.description,
        playback = Playback(resource.playback!!.thumbnailUrl!!, resource.playback!!.duration!!)
    )
}
