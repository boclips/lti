package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.exception.UnauthorizedException
import com.boclips.lti.core.application.model.SessionKeys
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import javax.servlet.http.HttpSession

class AssertHasValidSessionTest {
    @Test
    fun `does not throw an exception when given a valid LTI session`() {
        val session = mock<HttpSession> {
            on { getAttribute(SessionKeys.integrationId) } doReturn "test-integration"
        }
        assertThatCode { assertHasLtiSession(session) }.doesNotThrowAnyException()
    }

    @Test
    fun `does not throw an exception when given an LTI session with legacy consumerKy`() {
        val session = mock<HttpSession> {
            on { getAttribute(SessionKeys.consumerKey) } doReturn "test-integration"
        }
        assertThatCode { assertHasLtiSession(session) }.doesNotThrowAnyException()
    }

    @Test
    fun `throws UnauthorizedException if given session is null`() {
        assertThatThrownBy { assertHasLtiSession(null) }.isInstanceOf(UnauthorizedException::class.java)
    }

    @Test
    fun `throws UnauthorizedException if given session does not have integrationId attribute set`() {
        val session = mock<HttpSession> { }
        assertThatThrownBy { assertHasLtiSession(session) }.isInstanceOf(UnauthorizedException::class.java)
    }

    @Test
    fun `throws UnauthorizedException if given session does not have neither integrationId nor consumerKey set`() {
        val session = mock<HttpSession> {
            on { getAttribute(SessionKeys.integrationId) } doReturn null
            on { getAttribute(SessionKeys.consumerKey) } doReturn null
        }
        assertThatThrownBy { assertHasLtiSession(session) }.isInstanceOf(UnauthorizedException::class.java)
    }

    val assertHasLtiSession = AssertHasValidSession()
}
