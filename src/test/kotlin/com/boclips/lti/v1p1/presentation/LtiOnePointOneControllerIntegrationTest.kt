package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.domain.exception.LaunchRequestInvalidException
import com.boclips.lti.v1p1.domain.model.VideoMetadata
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p1.testsupport.CreateVideoRequestFactory
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.SubjectId
import com.boclips.videos.service.client.VideoId
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.endsWith
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.util.LinkedMultiValueMap

class VideosLtiOnePointOneControllerIntegrationTest : LtiOnePointOneControllerIntegrationTest() {
    @Test
    fun `valid video launch establishes an LTI session and resource can be correctly retrieved`() {
        val session = mvc.perform(
            post(resourcePath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLtiLaunchRequestPayload)
        ).andReturn().request.session

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("video"))
            .andExpect(model().attribute("videoUrl", endsWith(videoId)))
    }

    val videoId = "3928cc3830a14af9902e133e"
    override fun resourcePath() = "/v1p1/videos/$videoId"
}

class CollectionsLtiOnePointOneControllerIntegrationTest : LtiOnePointOneControllerIntegrationTest() {
    @Test
    fun `valid collection launch establishes an LTI session and resource can be correctly retrieved`() {
        populateCollection()

        val session = mvc.perform(
            post(resourcePath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLtiLaunchRequestPayload)
        ).andReturn().request.session

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("collection"))
            .andDo { result ->
                result.modelAndView!!.model["videos"]!!.let {
                    val videos = it as List<*>
                    assertThat(
                        videos
                            .filterIsInstance(VideoMetadata::class.java)
                            .map { videoMetadata -> videoMetadata.videoUrl.substringAfterLast("/") }
                    )
                        .containsExactly(firstVideoId.value, secondVideoId.value, thirdVideoId.value)
                }
            }
    }

    lateinit var firstVideoId: VideoId
    lateinit var secondVideoId: VideoId
    lateinit var thirdVideoId: VideoId

    val collectionId = "87064254edd642a8a4c2e22a"

    override fun resourcePath() = "/v1p1/collections/$collectionId"

    private fun populateCollection() {
        videoServiceClient.apply {
            val subjects = setOf(SubjectId("Math"))

            firstVideoId = create(CreateVideoRequestFactory.create(contentProviderId = "firstContentProvider"))
            secondVideoId = create(CreateVideoRequestFactory.create(contentProviderId = "secondContentProvider"))
            thirdVideoId = create(CreateVideoRequestFactory.create(contentProviderId = "thirdContentProvider"))

            val videos = listOf(firstVideoId, secondVideoId, thirdVideoId).map(::get)

            addCollection(
                Collection.builder()
                    .collectionId(rawIdToCollectionId(collectionId))
                    .title("first collection")
                    .subjects(subjects)
                    .videos(videos)
                    .build()
            )
        }
    }
}

abstract class LtiOnePointOneControllerIntegrationTest : AbstractSpringIntegrationTest() {
    abstract fun resourcePath(): String

    @Test
    fun `endpoint redirects user to landing page if it receives a minimal correct request`() {
        mvc.perform(
            post(resourcePath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLtiLaunchRequestPayload)
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", resourcePath()))
    }

    @Nested
    @DisplayName(value = "Invalid Requests")
    inner class InvalidRequests {
        @Test
        fun `endpoint returns an error if request misses resource_link_id`() {
            mvc.perform(
                post(resourcePath())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(
                        prepareLaunchRequest(
                            mapOf(
                                "lti_message_type" to "basic-lti-launch-request",
                                "lti_version" to "LTI-1p0",
                                "oauth_consumer_key" to ltiProperties.consumer.key
                            ),
                            ltiProperties.consumer.key,
                            ltiProperties.consumer.secret
                        )
                    )
            )
                .andExpect(status().isBadRequest)
                .andDo { result: MvcResult ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(LaunchRequestInvalidException::class.java)
                        .hasMessage("LTI resource link id was not provided")
                }
        }

        @Test
        fun `endpoint returns an error if it receives a blank request`() {
            mvc.perform(
                post(resourcePath())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
                .andExpect(status().isBadRequest)
                .andDo { result: MvcResult ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(LaunchRequestInvalidException::class.java)
                        .hasMessageContaining("LTI launch verification failed")
                }
        }
    }

    @Test
    fun `if request is invalid do not set a session`() {
        val session = mvc.perform(
            post(resourcePath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andReturn().request.session

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `accessing a video without a session should result in unauthorised response`() {
        mvc.perform(get(resourcePath()))
            .andExpect(status().isUnauthorized)
    }

    lateinit var validLtiLaunchRequestPayload: LinkedMultiValueMap<String, String>

    @BeforeEach
    fun setUp() {
        validLtiLaunchRequestPayload = prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to ltiProperties.consumer.key,
                "resource_link_id" to "41B464BA-F406-485C-ACDF-C1E5EB474156"
            ),
            ltiProperties.consumer.key,
            ltiProperties.consumer.secret
        )
    }

    private fun prepareLaunchRequest(
        parameters: Map<String, String>,
        key: String,
        secret: String
    ): LinkedMultiValueMap<String, String> {

        val signedParameters = ltiOauthSigner.signParameters(
            parameters,
            key,
            secret,
            "http://localhost${resourcePath()}",
            "POST"
        )

        return LinkedMultiValueMap(signedParameters.mapValues { listOf(it.value) })
    }
}
