package com.boclips.lti.v1p3.presentation

import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.v1p3.presentation.model.ContentSelectionRequest
import com.boclips.lti.v1p3.presentation.model.DeepLinkingResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/v1p3/deep-linking-response")
class DeepLinkingResponseController(private val assertHasValidSession: AssertHasValidSession) {
    @PostMapping
    fun generateDeepLinkingResponseForSelection(
        @RequestBody contentSelection: ContentSelectionRequest,
        httpSession: HttpSession
    ): DeepLinkingResponse {
        assertHasValidSession(httpSession)

        return DeepLinkingResponse(jwt = "this can be anything for now")
    }
}
