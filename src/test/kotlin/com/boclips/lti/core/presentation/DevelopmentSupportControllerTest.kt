package com.boclips.lti.core.presentation

import com.boclips.lti.core.configuration.properties.DevSupportProperties
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import com.boclips.videos.api.response.video.VideoResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DevelopmentSupportControllerTest : AbstractSpringIntegrationTest() {
    @Test
    fun `sets up a back door session and can retrieve resources`() {
        val session = mvc.perform(post("/dev-support/initialise-session"))
            .andExpect(status().isOk)
            .andReturn().request.session

        mvc.perform(get("/videos/${video.id}").session(session as MockHttpSession))
            .andExpect(status().isOk)
    }

    @Autowired
    lateinit var devSupportProperties: DevSupportProperties

    lateinit var video: VideoResource

    @BeforeEach
    fun populateCollection() {
        video =
            (videosClientFactory.getClient(devSupportProperties.integrationId) as VideosClientFake).add(
                VideoResourcesFactory.sampleVideo()
            )
    }
}
