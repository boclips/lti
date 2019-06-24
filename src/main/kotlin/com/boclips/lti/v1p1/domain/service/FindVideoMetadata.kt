package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p1.domain.model.VideoMetadata
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.videos.service.client.VideoId
import com.boclips.videos.service.client.exceptions.VideoNotFoundException
import org.springframework.stereotype.Service

@Service
class FindVideoMetadata(
    private val videoRepository: VideoRepository,
    private val uriComponentsBuilderFactory: UriComponentsBuilderFactory
) {
    operator fun invoke(videoId: VideoId): VideoMetadata? {
        try {
            return videoRepository
                .get(videoId)
                .run {
                    VideoMetadata(
                        uriComponentsBuilderFactory.getInstance()
                            .replacePath("/v1p1/videos/${videoId.value}")
                            .toUriString(),
                        title,
                        description,
                        playback.thumbnailUrl,
                        playback.duration
                    )
                }
        }
        catch (e: VideoNotFoundException) {
            return null
        }
    }
}
