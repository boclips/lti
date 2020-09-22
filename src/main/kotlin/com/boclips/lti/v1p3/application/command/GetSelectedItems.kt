package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.domain.model.VideoRequest
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.application.model.SelectedVideo
import com.boclips.lti.v1p3.presentation.model.SelectedVideoRequest

class GetSelectedItems(
    private val resourceLinkService: ResourceLinkService,
    private val videoRepository: VideoRepository
) {
    operator fun invoke(selectedItems: List<SelectedVideoRequest>, integrationId: String): List<SelectedVideo> {
        return selectedItems.map {
            val video = videoRepository.get(
                VideoRequest(
                    videoId = it.id,
                    integrationId = integrationId
                )
            )
            SelectedVideo(
                url = resourceLinkService.getEmbeddableVideoLink(video),
                title = video.title,
                text = video.description ?: ""
            )
        }
    }
}
