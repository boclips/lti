package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import com.boclips.lti.core.application.service.ApiAccessTokenProvider
import com.boclips.lti.core.application.service.AssertHasValidSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
class AccessTokenController(
    private val apiAccessTokenProvider: ApiAccessTokenProvider,
    private val assertHasValidSession: AssertHasValidSession
) {
    @GetMapping("/auth/token")
    fun getToken(session: HttpSession): String {
        assertHasValidSession(session)
        return apiAccessTokenProvider.getAccessToken(session.getAttribute(consumerKey) as String)
    }
}
