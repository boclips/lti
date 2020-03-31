package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.application.exception.MissingSessionAttributeException
import com.boclips.lti.v1p3.domain.model.SessionKeys
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpSession
import com.boclips.lti.core.application.model.SessionKeys as CoreSessionKeys

class LtiOnePointThreeSessionTest {
    @Test
    fun `returns a state`() {
        val stateValue = "the best state ever"
        val httpSession = MockHttpSession()
        httpSession.setAttribute(SessionKeys.state, stateValue)

        val ltiSession = LtiOnePointThreeSession(httpSession)

        assertThat(ltiSession.getState()).isEqualTo(stateValue)
    }

    @Test
    fun `throws an exception if state value is missing`() {
        val httpSession = MockHttpSession()

        val ltiSession = LtiOnePointThreeSession(httpSession)

        assertThatThrownBy { ltiSession.getState() }
            .isInstanceOf(MissingSessionAttributeException::class.java)
            .hasMessageContaining("state")
    }

    @Test
    fun `returns a targetLinkUri`() {
        val targetLinkUriValue = "the best resource ever"
        val httpSession = MockHttpSession()
        httpSession.setAttribute(SessionKeys.targetLinkUri, targetLinkUriValue)

        val ltiSession = LtiOnePointThreeSession(httpSession)

        assertThat(ltiSession.getTargetLinkUri()).isEqualTo(targetLinkUriValue)
    }

    @Test
    fun `sets a target link uri`() {
        val httpSession = MockHttpSession()

        val ltiSession = LtiOnePointThreeSession(httpSession)

        val uri = "https://super.com/ok"
        ltiSession.setTargetLinkUri(uri)

        assertThat(httpSession.getAttribute(SessionKeys.targetLinkUri)).isEqualTo(uri)
    }

    @Test
    fun `throws an exception if targetLinkUri value is missing`() {
        val httpSession = MockHttpSession()

        val ltiSession = LtiOnePointThreeSession(httpSession)

        assertThatThrownBy { ltiSession.getTargetLinkUri() }
            .isInstanceOf(MissingSessionAttributeException::class.java)
            .hasMessageContaining("targetLinkUri")
    }

    @Test
    fun `sets an integrationId`() {
        val httpSession = MockHttpSession()

        val ltiSession = LtiOnePointThreeSession(httpSession)

        ltiSession.setIntegrationId("hello")

        assertThat(httpSession.getAttribute(CoreSessionKeys.integrationId)).isEqualTo("hello")
    }

    @Test
    fun `retrieves an integrationId`() {
        val httpSession = MockHttpSession()

        val ltiSession = LtiOnePointThreeSession(httpSession)
        ltiSession.setIntegrationId("hello")

        assertThat(ltiSession.getIntegrationId()).isEqualTo("hello")
    }
}
