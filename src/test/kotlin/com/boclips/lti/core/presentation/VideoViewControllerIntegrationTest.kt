package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.infrastructure.repository.VideoResourceConverter
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.LtiTestSession
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import com.boclips.videos.api.response.video.VideoResource
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class VideoViewControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `accessing a video without a session results in unauthorised response`() {
        mvc.perform(get("/videos/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `video view is rendered correctly when user has a valid session`() {
        val video = VideoResourceConverter.toVideo(videoResource)

        val session = LtiTestSession.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/videos/${video.videoId.value}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("video"))
            .andExpect(model().attribute("video", toVideoViewModel(video)))
    }

    @Test
    fun `frame embedding protection is disabled`() {
        val video = VideoResourceConverter.toVideo(videoResource)

        val session = LtiTestSession.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/videos/${video.videoId.value}").session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
    }

    @Test
    fun `userId is passed into the view when it's in the session`() {
        val testUserId = "test-user-id"

        val session = LtiTestSession.authenticated(
            integrationId = integrationId,
            sessionAttributes = mapOf(
                SessionKeys.userId to testUserId
            )
        )

        mvc.perform(get("/videos/${videoResource.id}").session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("video"))
            .andExpect(model().attribute("userId", testUserId))
    }

    @Test
    fun `sets partner logo`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = LtiTestSession.authenticated(
            integrationId = integrationId,
            sessionAttributes = mapOf(
                SessionKeys.customLogo to testLogoUri
            )
        )

        mvc.perform(get("/videos/${videoResource.id}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    @Test
    fun `does not set partner logo if it's not set in the session`() {
        val session = LtiTestSession.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/videos/${videoResource.id}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", nullValue()))
    }

    @Test
    fun `returns a 404 response when requested video is not found`() {
        mvc.perform(
                get("/videos/this-does-not-exit")
                    .session(LtiTestSession.authenticated(integrationId = integrationId) as MockHttpSession)
            )
            .andExpect(status().isNotFound)
    }

    private val integrationId = "test-integration"

    private lateinit var videoResource: VideoResource

    @BeforeEach
    fun insertVideoFixture() {
        videoResource = VideoResourcesFactory.sampleVideo()
        (videosClientFactory.getClient(integrationId) as VideosClientFake).add(videoResource)
    }
}
