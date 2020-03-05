package com.boclips.lti.v1p1.presentation

import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.v1p1.application.service.VideoServiceAccessTokenProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
class LtiAuthController(
    private val videoServiceAccessTokenProvider: VideoServiceAccessTokenProvider,
    private val assertHasValidSession: AssertHasValidSession
) {
    @GetMapping("/auth/token")
    fun getToken(session: HttpSession): String {
        assertHasValidSession(session)
        return videoServiceAccessTokenProvider.getAccessToken(session.getAttribute(consumerKey) as String)
    }
}
