package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.presentation.model.CollectionViewModel
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.CollectionResourceFactory
import com.boclips.lti.testsupport.factories.LtiTestSessionFactory
import com.boclips.videos.api.httpclient.test.fakes.CollectionsClientFake
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

class UserCollectionsViewControllerIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `accessing user collections without a session results in unauthorised response`() {
        mvc.perform(MockMvcRequestBuilders.get("/collections"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `if session is invalidated, display error page`() {
        val session = LtiTestSessionFactory.authenticated(
            integrationId = "BLAH",
            sessionAttributes = mapOf("lastAccessedTime" to 99999999999)
        ) as MockHttpSession

        session.invalidate()

        mvc.perform(MockMvcRequestBuilders.get("/collections").session(session))
            .andExpect(status().isUnauthorized)
            .andExpect(view().name("error/invalidSession"))
    }

    @Test
    fun `valid user collections launch establishes an LTI session and user collections page can be correctly accessed`() {
        val firstCollection = (collectionsClientFactory.getClient(integrationId) as CollectionsClientFake).add(
            CollectionResourceFactory.sample(title = "First collection")
        )
        val secondCollection = (collectionsClientFactory.getClient(integrationId) as CollectionsClientFake).add(
            CollectionResourceFactory.sample(title = "Second collection")
        )

        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(MockMvcRequestBuilders.get("/collections").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(view().name("userCollections"))
            .andDo { result ->
                result.modelAndView!!.model["collections"]!!.let {
                    val collections = it as List<*>
                    assertThat(
                        collections
                            .filterIsInstance(CollectionViewModel::class.java)
                            .map { collection -> collection.collectionPageUrl.substringAfterLast("/") }
                    )
                        .containsExactly(firstCollection.id, secondCollection.id)
                }
            }
    }

    @Test
    fun `frame embedding protection is disabled`() {
        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(MockMvcRequestBuilders.get("/collections").session(session as MockHttpSession))
            .andExpect(header().doesNotExist("X-Frame-Options"))
    }

    @Test
    fun `sets partner logo on user collections page`() {
        val testLogoUri = "https://images.com/partner/custom/logo.png"

        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId,
            sessionAttributes = mapOf(
                SessionKeys.customLogo to testLogoUri
            )
        )

        mvc.perform(MockMvcRequestBuilders.get("/collections").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", testLogoUri))
    }

    @Test
    fun `does not set partner logo if it's not set in the session`() {
        val session = LtiTestSessionFactory.authenticated(
            integrationId = integrationId
        )

        mvc.perform(MockMvcRequestBuilders.get("/collections").session(session as MockHttpSession))
            .andExpect(status().isOk)
            .andExpect(model().attribute("customLogoUrl", nullValue()))
    }

    private val integrationId = "hey-ho"
}
