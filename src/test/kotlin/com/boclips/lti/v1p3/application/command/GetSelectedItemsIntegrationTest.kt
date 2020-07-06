package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.domain.exception.ResourceNotFoundException
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.lti.v1p3.presentation.model.SelectedVideoRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class GetSelectedItemsIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `returns an empty list when given an empty list`() {
        assertThat(getSelectedItems(emptyList(), integrationId)).isEmpty()
    }

    @Test
    fun `returns a list of selected videos`() {
        val firstResource = VideoResourcesFactory.sampleVideo(videoId = "123")
        val secondResource = VideoResourcesFactory.sampleVideo(videoId = "456")
        saveVideo(firstResource, integrationId)
        saveVideo(secondResource, integrationId)

        val selectionRequest = listOf(SelectedVideoRequest("123"), SelectedVideoRequest("456"))

        val selectedItems = getSelectedItems(selectionRequest, integrationId)

        assertThat(selectedItems.map { it.url.toString() }).containsExactlyInAnyOrder(
            "http://localhost/videos/123",
            "http://localhost/videos/456"
        )
    }

    @Test
    fun `throws when it's not able to find a video`() {
        assertThatThrownBy { getSelectedItems(listOf(SelectedVideoRequest("phantom")), integrationId) }
            .isInstanceOf(ResourceNotFoundException::class.java)
    }

    @Autowired
    private lateinit var getSelectedItems: GetSelectedItems

    private val integrationId = "integration-id"
}
