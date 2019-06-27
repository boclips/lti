package com.boclips.lti.v1p1.domain.service

import com.boclips.lti.v1p1.application.exception.UnauthorizedException
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.authenticationStateHolder
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import javax.servlet.http.HttpSession

class AssertHasLtiSessionTest {
    @Test
    fun `does not throw an exception when given a valid LTI session`() {
        val session = mock<HttpSession> {
            on { getAttribute(authenticationStateHolder) } doReturn true
        }
        assertThatCode { assertHasLtiSession(session) }.doesNotThrowAnyException()
    }

    @Test
    fun `throws UnauthorizedException if given session is null`() {
        assertThatThrownBy { assertHasLtiSession(null) }.isInstanceOf(UnauthorizedException::class.java)
    }

    @Test
    fun `throws UnauthorizedException if given session does not have isAuthenticated attribute`() {
        val session = mock<HttpSession> { }
        assertThatThrownBy { assertHasLtiSession(session) }.isInstanceOf(UnauthorizedException::class.java)
    }

    @Test
    fun `throws UnauthorizedException if given session has isAuthenticated = false`() {
        val session = mock<HttpSession> {
            on { getAttribute(authenticationStateHolder) } doReturn false
        }
        assertThatThrownBy { assertHasLtiSession(session) }.isInstanceOf(UnauthorizedException::class.java)
    }

    val assertHasLtiSession = AssertHasLtiSession()
}
