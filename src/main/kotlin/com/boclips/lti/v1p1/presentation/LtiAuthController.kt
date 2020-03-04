package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.application.service.VideoServiceAccessTokenProvider
import com.boclips.lti.v1p1.domain.service.AssertHasLtiSession
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.consumerKeyHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
class LtiAuthController(
    private val videoServiceAccessTokenProvider: VideoServiceAccessTokenProvider,
    private val assertHasLtiSession: AssertHasLtiSession
) {
    @GetMapping("/auth/token")
    fun getToken(session: HttpSession): String {
        assertHasLtiSession(session)
        return videoServiceAccessTokenProvider.getAccessToken(session.getAttribute(consumerKeyHolder) as String)
    }
}
