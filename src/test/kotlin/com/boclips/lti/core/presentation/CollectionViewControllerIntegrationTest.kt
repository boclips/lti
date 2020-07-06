package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.presentation.model.VideoViewModel
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.CollectionResourceFactory
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.response.collection.CollectionResource
import com.boclips.videos.api.response.video.VideoResource
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class CollectionViewControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `accessing a collection without a session results in unauthorised response`() {
        mvc.perform(get("/collections/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `displays a collection page with expected data`() {
        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/collections/${collection.id}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("collection"))
            .andExpect(model().attribute("collectionTitle", collection.title!!))
            .andDo { result ->
                result.modelAndView!!.model["videos"]!!.let {
                    val videos = it as List<*>
                    assertThat(
                        videos
                            .filterIsInstance(VideoViewModel::class.java)
                            .map { videoMetadata -> videoMetadata.videoPageUrl.substringAfterLast("/") }
                    )
                        .containsExactly(firstVideo.id, secondVideo.id, thirdVideo.id)
                }
            }
    }

    @Test
    fun `frame embedding protection is disabled`() {
        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/collections/${collection.id}").session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
    }

    @Test
    fun `sets partner logo on collection page`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId,
            sessionAttributes = mapOf(
                SessionKeys.customLogo to testLogoUri
            )
        )

        mvc.perform(get("/collections/${collection.id}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    @Test
    fun `does not set partner logo if it's not set in the session`() {
        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(get("/collections/${collection.id}").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", nullValue()))
    }

    @Test
    fun `returns a 404 response when requested collection is not found`() {
        mvc.perform(
            get("/collections/this-does-not-exit")
                .session(LtiTestSessionFactory.authenticated(integrationId = integrationId) as MockHttpSession)
        )
            .andExpect(status().isNotFound)
    }

    val integrationId = "test-consumer"

    lateinit var firstVideo: VideoResource
    lateinit var secondVideo: VideoResource
    lateinit var thirdVideo: VideoResource

    lateinit var collection: CollectionResource

    @BeforeEach
    fun populateCollection() {
        firstVideo = saveVideo(VideoResourcesFactory.sampleVideo(), integrationId)
        secondVideo = saveVideo(VideoResourcesFactory.sampleVideo(), integrationId)
        thirdVideo = saveVideo(VideoResourcesFactory.sampleVideo(), integrationId)

        collection = (collectionsClientFactory.getClient(integrationId) as CollectionsClientFake).add(
            CollectionResourceFactory.sample(videos = listOf(firstVideo, secondVideo, thirdVideo))
        )
    }
}
