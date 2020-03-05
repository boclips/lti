package com.boclips.lti.core.infrastructure.repository

import com.boclips.lti.core.domain.model.Playback
import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.domain.model.VideoId
import com.boclips.lti.v1p1.infrastructure.repository.exception.ResourceConversionException
import com.boclips.videos.api.request.video.PlaybackResource
import com.boclips.videos.api.response.video.VideoResource
import org.springframework.hateoas.Link
import java.net.URI

object VideoResourceConverter {
    private const val DEFAULT_THUMBNAIL_WIDTH = 500

    fun toVideo(resource: VideoResource) = Video(
        videoId = VideoId(
            value = resource.id!!,
            uri = URI(resource._links!!["self"]?.href!!)
        ),
        title = resource.title!!,
        description = resource.description,
        playback = Playback(
            convertThumbnailUrl(
                resource.playback!!
            ),
            resource.playback!!.duration!!
        )
    )

    private fun convertThumbnailUrl(playbackResource: PlaybackResource): String {
        val rawThumbnailLink =
            playbackResource._links!!["thumbnail"] ?: throw ResourceConversionException("Thumbnail link not available")

        // We need this because as of today Spring HATEOAS does not deserialize links correctly
        val thumbnailLink = Link(rawThumbnailLink.href)

        return if (thumbnailLink.isTemplated) {
            thumbnailLink.template.expand(mapOf("thumbnailWidth" to DEFAULT_THUMBNAIL_WIDTH)).toString()
        } else {
            thumbnailLink.href
        }
    }
}
