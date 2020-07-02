package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.model.DeepLinkingSelection
import com.boclips.lti.v1p3.application.model.SelectedVideo
import java.util.UUID

object DeepLinkingSelectionFactory {
    fun sample(
        deploymentId: String = UUID.randomUUID().toString(),
        data: String = "something",
        selectedVideos: List<SelectedVideo> = emptyList()
    ) = DeepLinkingSelection(
        deploymentId = deploymentId,
        data = data,
        selectedVideos = selectedVideos
    )
}
