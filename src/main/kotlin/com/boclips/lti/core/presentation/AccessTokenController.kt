package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.service.ApiAccessTokenProvider
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.application.service.LtiSessionHelpers.getIntegrationId
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
class AccessTokenController(
    private val apiAccessTokenProvider: ApiAccessTokenProvider,
    private val assertHasValidSession: AssertHasValidSession
) {
    @CrossOrigin(origins = ["*"], allowCredentials = "true")
    @GetMapping("/auth/token")
    fun getToken(session: HttpSession): String {
        assertHasValidSession(session)
        return apiAccessTokenProvider.getAccessToken(getIntegrationId(session))
    }
}
