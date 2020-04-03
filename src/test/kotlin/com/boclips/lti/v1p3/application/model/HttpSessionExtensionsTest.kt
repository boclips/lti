package com.boclips.lti.v1p3.application.model

import com.boclips.lti.v1p3.application.exception.MissingSessionAttributeException
import com.boclips.lti.v1p3.domain.model.SessionKeys
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession

class HttpSessionExtensionsTest {
    @Test
    fun `returns a state`() {
        val stateValue = "the best state ever"

        val httpSession = MockHttpSession()
        httpSession.setAttribute(SessionKeys.state, stateValue)

        assertThat(httpSession.getState()).isEqualTo(stateValue)
    }

    @Test
    fun `throws an exception if state value is missing`() {
        val httpSession = MockHttpSession()

        assertThatThrownBy { httpSession.getState() }
            .isInstanceOf(MissingSessionAttributeException::class.java)
            .hasMessageContaining("state")
    }

    @Test
    fun `returns a targetLinkUri`() {
        val targetLinkUriValue = "the best resource ever"
        val httpSession = MockHttpSession()
        httpSession.setAttribute(SessionKeys.targetLinkUri, targetLinkUriValue)

        assertThat(httpSession.getTargetLinkUri()).isEqualTo(targetLinkUriValue)
    }

    @Test
    fun `sets a target link uri`() {
        val httpSession = MockHttpSession()

        val uri = "https://super.com/ok"
        httpSession.setTargetLinkUri(uri)

        assertThat(httpSession.getAttribute(SessionKeys.targetLinkUri)).isEqualTo(uri)
    }

    @Test
    fun `throws an exception if targetLinkUri value is missing`() {
        val httpSession = MockHttpSession()

        assertThatThrownBy { httpSession.getTargetLinkUri() }
            .isInstanceOf(MissingSessionAttributeException::class.java)
            .hasMessageContaining("targetLinkUri")
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
