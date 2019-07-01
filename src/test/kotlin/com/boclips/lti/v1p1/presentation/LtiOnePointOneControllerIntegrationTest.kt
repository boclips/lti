package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.domain.exception.LaunchRequestInvalidException
import com.boclips.lti.v1p1.domain.model.CustomLaunchParams
import com.boclips.lti.v1p1.presentation.model.VideoMetadata
import com.boclips.lti.v1p1.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p1.testsupport.CreateVideoRequestFactory
import com.boclips.videos.service.client.Collection
import com.boclips.videos.service.client.SubjectId
import com.boclips.videos.service.client.Video
import com.boclips.videos.service.client.VideoId
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
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
import java.util.UUID
import javax.servlet.http.HttpSession

@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class VideosLtiOnePointOneControllerIntegrationTest : LtiOnePointOneControllerIntegrationTest() {
    @Test
    fun `valid video launch establishes an LTI session and resource can be correctly retrieved`() {
        val session = executeLtiLaunch()

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("video"))
            .andExpect(model().attribute("video", toVideoMetadata(video)))
    }

    lateinit var videoIdString: String
    lateinit var video: Video

    override fun resourcePath() = interpolateResourcePath(videoIdString)

    override fun interpolateResourcePath(resourceId: String) = "/v1p1/videos/$resourceId"

    override fun earlySetup() {
        videoServiceClient.apply {
            val videoId = create(CreateVideoRequestFactory.create(contentProviderId = UUID.randomUUID().toString()))
            videoIdString = videoId.value
            video = get(videoId)
        }
    }
}

@EnableAutoConfiguration(exclude = [MongoAutoConfiguration::class, MongoDataAutoConfiguration::class])
class CollectionsLtiOnePointOneControllerIntegrationTest : LtiOnePointOneControllerIntegrationTest() {
    @Test
    fun `valid collection launch establishes an LTI session and resource can be correctly retrieved`() {
        val session = executeLtiLaunch()

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("collection"))
            .andExpect(model().attribute("collectionTitle", collectionTitle))
            .andDo { result ->
                result.modelAndView!!.model["videos"]!!.let {
                    val videos = it as List<*>
                    assertThat(
                        videos
                            .filterIsInstance(VideoMetadata::class.java)
                            .map { videoMetadata -> videoMetadata.videoPageUrl.substringAfterLast("/") }
                    )
                        .containsExactly(firstVideoId.value, secondVideoId.value, thirdVideoId.value)
                }
            }
    }

    @Test
    fun `preserves partner logo on response model for videos accessed from collection page`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = executeLtiLaunch(mapOf(
            CustomLaunchParams.LOGO to testLogoUri
        ))

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))

        mvc.perform(get("/v1p1/videos/${firstVideoId.value}").session(session))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    @BeforeEach
    fun setUpCollectionTest() {
        videoServiceClient.clear()
        populateCollection()
    }

    lateinit var firstVideoId: VideoId
    lateinit var secondVideoId: VideoId
    lateinit var thirdVideoId: VideoId

    val collectionId = "87064254edd642a8a4c2e22a"
    val collectionTitle = "first collection"

    override fun resourcePath() = "/v1p1/collections/$collectionId"

    override fun interpolateResourcePath(resourceId: String) = "/v1p1/collections/$resourceId"

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
                    .title(collectionTitle)
                    .subjects(subjects)
                    .videos(videos)
                    .build()
            )
        }
    }
}

abstract class LtiOnePointOneControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `endpoint redirects user to landing page if it receives a minimal correct request`() {
        mvc.perform(
            post(resourcePath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLaunchRequest())
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

    @Test
    fun `returns a 404 response when requested resource is not found`() {
        val session = executeLtiLaunch()

        mvc.perform(get(interpolateResourcePath(invalidResourceId)).session(session as MockHttpSession))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `adds partner logo to response model if it's provided in LTI launch and preserves it over subsequent access`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = executeLtiLaunch(mapOf(
            CustomLaunchParams.LOGO to testLogoUri
        ))

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    @Test
    fun `does not set partner logo on response model if it's not provided in LTI launch`() {
        val session = executeLtiLaunch()

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", nullValue()))
    }

    @Test
    fun `does not set partner logo on response model if empty value is provided in LTI launch`() {
        val session = executeLtiLaunch(mapOf(
            CustomLaunchParams.LOGO to ""
        ))

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", nullValue()))
    }

    abstract fun resourcePath(): String
    abstract fun interpolateResourcePath(resourceId: String): String
    fun earlySetup() = Unit

    val invalidResourceId = "000000000000000000000000"

    @BeforeEach
    fun setUp() {
        earlySetup()
    }

    protected fun executeLtiLaunch(customParameters: Map<String, String> = emptyMap()): HttpSession? {
        return mvc.perform(
            post(resourcePath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLaunchRequest(customParameters))
        ).andReturn().request.session
    }

    protected fun validLaunchRequest(customParameters: Map<String, String> = emptyMap()): LinkedMultiValueMap<String, String> {
        return prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to ltiProperties.consumer.key,
                "resource_link_id" to "41B464BA-F406-485C-ACDF-C1E5EB474156"
            ) + customParameters,
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
