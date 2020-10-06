package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.infrastructure.repository.VideoResourceConverter
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.videos.api.response.video.VideoResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class EmbeddableVideoViewControllerIntegrationTest : AbstractSpringIntegrationTest() {
    private val integrationId = "test-integration"
    private lateinit var videoResource: VideoResource

    @BeforeEach
    fun insertVideoFixture() {
        videoResource = VideoResourcesFactory.sampleVideo()
        saveVideo(videoResource, integrationId)
    }

    @Test
    fun `accessing a video embed without a session results in unauthorised response`() {
        mvc.perform(get("/embeddable-videos/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `embeddable video view is rendered correctly when user has a valid session`() {
        val video = VideoResourceConverter.toVideo(videoResource)

        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/embeddable-videos/${video.videoId.value}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("embeddable-video"))
            .andExpect(model().attribute("video", toVideoViewModel(video)))
    }

    @Test
    fun `userId is passed into the view when it's in the session`() {
        val testUserId = "test-user-id"

        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId,
            sessionAttributes = mapOf(
                SessionKeys.boclipsUserId to testUserId
            )
        )

        mvc.perform(get("/embeddable-videos/${videoResource.id}").session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("userId", testUserId))
    }
}
