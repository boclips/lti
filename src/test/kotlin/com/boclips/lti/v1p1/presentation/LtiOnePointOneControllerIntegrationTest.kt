package com.boclips.lti.v1p1.presentation

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.CollectionResourceFactory
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.lti.v1p1.domain.exception.LaunchRequestInvalidException
import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.util.LinkedMultiValueMap
import javax.servlet.http.HttpSession

class LtiOnePointOneControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `launch redirects user to requested resource if it receives a correct request`() {
        mvc.perform(
            post("/v1p1/collections")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLaunchRequestParameters("/v1p1/collections"))
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", "/collections"))
    }

    @Nested
    inner class LaunchingResources {
        @Test
        fun `can launch into a video page`() {
            val video = VideoResourcesFactory.sampleVideo()
            saveVideo(video, consumerKey)

            val session = executeLtiLaunch(
                launchRequestPath = "/v1p1/videos/${video.id}"
            )

            mvc.perform(get("/videos/${video.id}").session(session as MockHttpSession))
                .andExpect(status().isOk)
                .andExpect(view().name("video"))
        }

        @Test
        fun `can launch into a collection page`() {
            val collection =
                (collectionsClientFactory.getClient(consumerKey) as CollectionsClientFake).add(CollectionResourceFactory.sample())

            val session = executeLtiLaunch(
                launchRequestPath = "/v1p1/collections/${collection.id}"
            )

            mvc.perform(get("/collections/${collection.id}").session(session as MockHttpSession))
                .andExpect(status().isOk)
                .andExpect(view().name("collection"))
        }

        @Test
        fun `can launch into a user collections page`() {
            val session = executeLtiLaunch(
                launchRequestPath = "/v1p1/collections"
            )

            mvc.perform(get("/collections").session(session as MockHttpSession))
                .andExpect(status().isOk)
                .andExpect(view().name("userCollections"))
        }
    }

    @Nested
    inner class InvalidRequests {
        @Test
        fun `returns an error if request misses resource_link_id`() {
            mvc.perform(
                post("/v1p1/collections")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(
                        sign(
                            launchRequestPath = "/v1p1/collections",
                            parameters = mapOf(
                                "lti_message_type" to "basic-lti-launch-request",
                                "lti_version" to "LTI-1p0",
                                "oauth_consumer_key" to consumerKey
                            ),
                            key = consumerKey,
                            secret = consumerSecret
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
        fun `returns an error when request misses consumer key`() {
            mvc.perform(
                post("/v1p1/collections")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(
                        validLaunchRequestParameters(
                            launchRequestPath = "/v1p1/collections"
                        )
                            .apply { remove("oauth_consumer_key") }
                    )
            )
                .andExpect(status().isBadRequest)
                .andDo { result: MvcResult ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(LaunchRequestInvalidException::class.java)
                        .hasMessage("LTI launch verification failed: bad_request because of Failed to validate: parameter_absent")
                }
        }

        @Test
        fun `returns an error when signature is invalid`() {
            mvc.perform(
                post("/v1p1/collections")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(
                        validLaunchRequestParameters(
                            launchRequestPath = "/v1p1/collections"
                        )
                            .apply { set("oauth_signature", listOf("this is not a valid signature")) }
                    )
            )
                .andExpect(status().isBadRequest)
                .andDo { result: MvcResult ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(LaunchRequestInvalidException::class.java)
                        .hasMessage("LTI launch verification failed: bad_request because of Failed to validate: signature_invalid")
                }
        }

        @Test
        fun `returns an error if it receives a blank request`() {
            mvc.perform(
                post("/v1p1/collections")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
                .andExpect(status().isBadRequest)
                .andDo { result: MvcResult ->
                    assertThat(result.resolvedException)
                        .isInstanceOf(LaunchRequestInvalidException::class.java)
                        .hasMessageContaining("LTI launch verification failed: bad_request because of Failed to validate: parameter_absent")
                }
        }

        @Test
        fun `does not set a session if request was invalid`() {
            val session = mvc.perform(
                post("/v1p1/collections")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            ).andReturn().request.session

            mvc.perform(get("/collections").session(session as MockHttpSession))
                .andExpect(status().isUnauthorized)
        }
    }

    val consumerSecret = "test-secret"
    val consumerKey = "test-consumer"

    @BeforeEach
    fun configureLtiCredentials() {
        ltiOnePointOneConsumerRepository.insert(
            LtiOnePointOneConsumerDocument(
                id = ObjectId(),
                key = consumerKey,
                secret = consumerSecret
            )
        )
    }

    private fun executeLtiLaunch(
        launchRequestPath: String,
        customParameters: Map<String, String> = emptyMap()
    ): HttpSession? {
        return mvc.perform(
            post(launchRequestPath)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLaunchRequestParameters(launchRequestPath, customParameters))
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", launchRequestPath.substringAfter("/v1p1")))
            .andReturn().request.session
    }

    private fun validLaunchRequestParameters(
        launchRequestPath: String,
        customParameters: Map<String, String> = emptyMap()
    ): LinkedMultiValueMap<String, String> {
        return sign(
            launchRequestPath = launchRequestPath,
            parameters = mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to consumerKey,
                "resource_link_id" to "41B464BA-F406-485C-ACDF-C1E5EB474156"
            ) + customParameters,
            key = consumerKey,
            secret = consumerSecret
        )
    }

    private fun sign(
        launchRequestPath: String,
        parameters: Map<String, String>,
        key: String,
        secret: String
    ): LinkedMultiValueMap<String, String> {

        val signedParameters = ltiOauthSigner.signParameters(
            parameters,
            key,
            secret,
            "http://localhost${launchRequestPath}",
            "POST"
        )

        return LinkedMultiValueMap(signedParameters.mapValues { listOf(it.value) })
    }
}
