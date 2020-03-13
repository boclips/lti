package com.boclips.lti.v1p1.presentation

import com.boclips.lti.core.domain.model.Video
import com.boclips.lti.core.infrastructure.repository.VideoResourceConverter
import com.boclips.lti.core.presentation.model.CollectionViewModel
import com.boclips.lti.core.presentation.model.VideoViewModel
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.CollectionResourceFactory
import com.boclips.lti.testsupport.factories.VideoResourcesFactory
import com.boclips.lti.v1p1.domain.exception.LaunchRequestInvalidException
import com.boclips.lti.v1p1.domain.model.LaunchParams
import com.boclips.lti.v1p1.infrastructure.model.LtiOnePointOneConsumerDocument
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import com.boclips.videos.api.httpclient.test.fakes.VideosClientFake
import com.boclips.videos.api.response.video.VideoResource
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.BeforeEach
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
import javax.servlet.http.HttpSession

class VideosLtiOnePointOneIntegrationTest : LtiOnePointOneIntegrationTest() {
    @Test
    fun `valid video launch establishes an LTI session and resource can be correctly retrieved`() {
        val testUserId = "test-user-id"
        val session = executeLtiLaunch(mapOf(LaunchParams.Custom.USER_ID to testUserId))

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("video"))
            .andExpect(model().attribute("video", toVideoViewModel(video)))
            .andExpect(model().attribute("userId", testUserId))
    }

    @Test
    fun `returns a 404 response when requested video is not found`() {
        val session = executeLtiLaunch()

        mvc.perform(get(interpolateResourcePath(invalidResourceId)).session(session as MockHttpSession))
            .andExpect(status().isNotFound)
    }

    lateinit var video: Video

    override fun resourcePath() = interpolateResourcePath(video.videoId.value)

    override fun interpolateResourcePath(resourceId: String?) = "/videos/$resourceId"

    @BeforeEach
    fun createVideo() {
        val resource = VideoResourcesFactory.sampleVideo()
        (videosClientFactory.getClient(consumerKey) as VideosClientFake).add(resource)
        video = VideoResourceConverter.toVideo(resource)
    }
}

class CollectionsLtiOnePointOneIntegrationTest : LtiOnePointOneIntegrationTest() {
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
                            .filterIsInstance(VideoViewModel::class.java)
                            .map { videoMetadata -> videoMetadata.videoPageUrl.substringAfterLast("/") }
                    )
                        .containsExactly(firstVideo.id, secondVideo.id, thirdVideo.id)
                }
            }
    }

    @Test
    fun `preserves partner logo on response model for videos accessed from collection page`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = executeLtiLaunch(
            mapOf(
                LaunchParams.Custom.LOGO to testLogoUri
            )
        )

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))

        mvc.perform(get("/videos/${firstVideo.id}").session(session))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    @Test
    fun `returns a 404 response when requested video is not found`() {
        val session = executeLtiLaunch()

        mvc.perform(get(interpolateResourcePath(invalidResourceId)).session(session as MockHttpSession))
            .andExpect(status().isNotFound)
    }

    lateinit var firstVideo: VideoResource
    lateinit var secondVideo: VideoResource
    lateinit var thirdVideo: VideoResource

    val collectionId = "87064254edd642a8a4c2e22a"
    val collectionTitle = "first collection"

    override fun resourcePath() = interpolateResourcePath(collectionId)

    override fun interpolateResourcePath(resourceId: String?) = "/collections/$resourceId"

    @BeforeEach
    fun populateCollection() {
        firstVideo =
            (videosClientFactory.getClient(consumerKey) as VideosClientFake).add(VideoResourcesFactory.sampleVideo())
        secondVideo =
            (videosClientFactory.getClient(consumerKey) as VideosClientFake).add(VideoResourcesFactory.sampleVideo())
        thirdVideo =
            (videosClientFactory.getClient(consumerKey) as VideosClientFake).add(VideoResourcesFactory.sampleVideo())

        (collectionsClientFactory.getClient(consumerKey) as CollectionsClientFake).add(
            CollectionResourceFactory.sample(
                id = collectionId,
                title = collectionTitle,
                videos = listOf(firstVideo, secondVideo, thirdVideo)
            )
        )
    }
}

class UserCollectionsLtiOnePointOneIntegrationTest : LtiOnePointOneIntegrationTest() {
    @Test
    fun `valid user collections launch establishes an LTI session and user collections page can be correctly accessed`() {
        val session = executeLtiLaunch()

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
            .andExpect(status().isOk)
            .andExpect(view().name("userCollections"))
            .andDo { result ->
                result.modelAndView!!.model["collections"]!!.let {
                    val collections = it as List<*>
                    assertThat(
                        collections
                            .filterIsInstance(CollectionViewModel::class.java)
                            .map { collectionMetadata -> collectionMetadata.collectionPageUrl.substringAfterLast("/") }
                    )
                        .containsExactly(firstCollectionId, secondCollectionId)
                }
            }
    }

    @Test
    fun `preserves partner logo on response model for collections accessed from user collection page`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = executeLtiLaunch(
            mapOf(
                LaunchParams.Custom.LOGO to testLogoUri
            )
        )

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))

        mvc.perform(get("/collections/$firstCollectionId").session(session))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    val firstCollectionId = "87064254edd642a8a4c2e22a"
    val secondCollectionId = "d02f78473e1545c6aa4a508a"

    override fun interpolateResourcePath(resourceId: String?) = "/collections"

    @BeforeEach
    fun populateCollections() {
        (collectionsClientFactory.getClient(consumerKey) as CollectionsClientFake).add(
            CollectionResourceFactory.sample(
                id = firstCollectionId,
                title = "First collection"
            )
        )
        (collectionsClientFactory.getClient(consumerKey) as CollectionsClientFake).add(
            CollectionResourceFactory.sample(
                id = secondCollectionId,
                title = "Second collection"
            )
        )
    }
}

abstract class LtiOnePointOneIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `endpoint redirects user to landing page if it receives a minimal correct request`() {
        mvc.perform(
            post(launchRequestPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLaunchRequest())
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", resourcePath()))
    }

    @Nested
    inner class InvalidRequests {
        @Test
        fun `endpoint returns an error if request misses resource_link_id`() {
            mvc.perform(
                post(launchRequestPath())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(
                        prepareLaunchRequest(
                            mapOf(
                                "lti_message_type" to "basic-lti-launch-request",
                                "lti_version" to "LTI-1p0",
                                "oauth_consumer_key" to consumerKey
                            ),
                            consumerKey,
                            consumerSecret
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
                post(launchRequestPath())
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
            post(launchRequestPath())
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
    fun `adds partner logo to response model if it's provided in LTI launch and preserves it over subsequent access`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = executeLtiLaunch(
            mapOf(
                LaunchParams.Custom.LOGO to testLogoUri
            )
        )

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
        val session = executeLtiLaunch(
            mapOf(
                LaunchParams.Custom.LOGO to ""
            )
        )

        mvc.perform(get(resourcePath()).session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", nullValue()))
    }

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

    val consumerSecret = "test-secret"
    val consumerKey = "test-consumer"

    private fun launchRequestPath() = "/v1p1${resourcePath()}"
    fun resourcePath() = interpolateResourcePath()
    abstract fun interpolateResourcePath(resourceId: String? = null): String

    val invalidResourceId = "000000000000000000000000"

    protected fun executeLtiLaunch(customParameters: Map<String, String> = emptyMap()): HttpSession? {
        return mvc.perform(
            post(launchRequestPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validLaunchRequest(customParameters))
        )
            .andExpect(status().isSeeOther)
            .andExpect(header().string("Location", resourcePath()))
            .andReturn().request.session
    }

    protected fun validLaunchRequest(customParameters: Map<String, String> = emptyMap()): LinkedMultiValueMap<String, String> {
        return prepareLaunchRequest(
            mapOf(
                "lti_message_type" to "basic-lti-launch-request",
                "lti_version" to "LTI-1p0",
                "oauth_consumer_key" to consumerKey,
                "resource_link_id" to "41B464BA-F406-485C-ACDF-C1E5EB474156"
            ) + customParameters,
            consumerKey,
            consumerSecret
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
            "http://localhost${launchRequestPath()}",
            "POST"
        )

        return LinkedMultiValueMap(signedParameters.mapValues { listOf(it.value) })
    }
}
