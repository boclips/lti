package com.boclips.lti.v1p3.domain.model

import com.boclips.lti.v1p3.application.exception.MissingSessionAttributeException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpSession

class HttpSessionExtensionsTest {
    @Test
    fun `returns true when session contains a mapping for a given state`() {
        val httpSession = MockHttpSession()
        val state = "test-state"

        httpSession.mapStateToTargetLinkUri(state, "this can be anything in this test")

        assertThat(httpSession.containsMappingForState(state)).isEqualTo(true)
    }

    @Test
    fun `returns false when session does not contain a target link URI mapping given state`() {
        val httpSession = MockHttpSession()

        assertThat(httpSession.containsMappingForState("this wasn't mapped")).isEqualTo(false)
    }

    @Test
    fun `maps target link URI to state`() {
        val httpSession = MockHttpSession()

        val state = "oregon"
        val uri = "https://super.com/ok"
        httpSession.mapStateToTargetLinkUri(state, uri)

        val statesToUrisMap = httpSession.getAttribute(SessionKeys.statesToTargetLinkUris) as Map<String, String>

        assertThat(statesToUrisMap).containsEntry(state, uri)
    }

    @Test
    fun `retrieves a target link URI mapped to a state`() {
        val httpSession = MockHttpSession()

        val state = "arkansas"
        val uri = "https://super.com/ok"
        httpSession.setAttribute(SessionKeys.statesToTargetLinkUris, mapOf(state to uri))

        assertThat(httpSession.getTargetLinkUri(state)).isEqualTo(uri)
    }

    @Test
    fun `throws an exception if no mapping is found for given state`() {
        val httpSession = MockHttpSession()

        assertThrows<MissingSessionAttributeException> {
            httpSession.getTargetLinkUri("this has not been added")
        }
    }

    @Test
    fun `maps multiple states and uris`() {
        val httpSession = MockHttpSession()

        val firstState = "arkansas"
        val firstUri = "https://super.com/ok"
        httpSession.mapStateToTargetLinkUri(firstState, firstUri)

        val secondState = "maine"
        val secondUri = "https://bummer.com/doh"
        httpSession.mapStateToTargetLinkUri(secondState, secondUri)

        assertThat(httpSession.getTargetLinkUri(firstState)).isEqualTo(firstUri)
        assertThat(httpSession.getTargetLinkUri(secondState)).isEqualTo(secondUri)
    }

    @Test
    fun `sets an integrationId`() {
        val httpSession = MockHttpSession()

        httpSession.setIntegrationId("hello")

        assertThat(httpSession.getAttribute(com.boclips.lti.core.application.model.SessionKeys.integrationId)).isEqualTo(
            "hello"
        )
    }

    @Test
    fun `retrieves an integrationId`() {
        val httpSession = MockHttpSession()
        httpSession.setIntegrationId("hello")

        assertThat(httpSession.getIntegrationId()).isEqualTo("hello")
    }
}
