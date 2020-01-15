package com.boclips.lti.v1p1.infrastructure.repository

import com.boclips.lti.v1p1.domain.model.Playback
import com.boclips.lti.v1p1.domain.model.Video
import com.boclips.lti.v1p1.domain.model.VideoId
import com.boclips.lti.v1p1.infrastructure.repository.exception.ResourceConversionException
import com.boclips.videos.api.request.video.PlaybackResource
import com.boclips.videos.api.response.video.VideoResource
import java.net.URI

object VideoResourceConverter {
    private const val DEFAULT_THUMBNAIL_WIDTH = 500

    fun toVideo(resource: VideoResource) = Video(
        videoId = VideoId(value = resource.id!!, uri = URI(resource._links!!["self"]?.href!!)),
        title = resource.title!!,
        description = resource.description,
        playback = Playback(convertThumbnailUrl(resource.playback!!), resource.playback!!.duration!!)
    )

    private fun convertThumbnailUrl(playbackResource: PlaybackResource): String {
        val thumbnailLink =
            playbackResource._links!!["thumbnail"] ?: throw ResourceConversionException("Thumbnail link not available")

        return if (thumbnailLink.isTemplated) {
            thumbnailLink.template.expand(mapOf("thumbnailWidth" to DEFAULT_THUMBNAIL_WIDTH)).toString()
        } else {
            thumbnailLink.href
        }
    }
}
