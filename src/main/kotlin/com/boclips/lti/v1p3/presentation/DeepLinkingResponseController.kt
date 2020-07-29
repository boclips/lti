package com.boclips.lti.v1p3.presentation

import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.v1p3.application.command.GetPlatformForIntegration
import com.boclips.lti.v1p3.application.command.GetSelectedItems
import com.boclips.lti.v1p3.application.model.DeepLinkingSelection
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.model.getIntegrationId
import com.boclips.lti.v1p3.presentation.model.ContentSelectionRequest
import com.boclips.lti.v1p3.presentation.model.DeepLinkingResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/v1p3/deep-linking-response")
class DeepLinkingResponseController(
    private val assertHasValidSession: AssertHasValidSession,
    private val jwtService: JwtService,
    private val getPlatformForIntegration: GetPlatformForIntegration,
    private val getSelectedItems: GetSelectedItems
) {
    @PostMapping
    fun generateDeepLinkingResponseForSelection(
        @RequestBody contentSelection: ContentSelectionRequest,
        httpSession: HttpSession
    ): ResponseEntity<DeepLinkingResponse> {
        assertHasValidSession(httpSession)

        val platform = getPlatformForIntegration(httpSession.getIntegrationId())
        val selectedItems = getSelectedItems(contentSelection.selectedItems, httpSession.getIntegrationId())

        val jwt = jwtService.createDeepLinkingResponseToken(
            platform,
            DeepLinkingSelection(
                deploymentId = contentSelection.deploymentId,
                selectedVideos = selectedItems,
                data = contentSelection.data
            )
        )

        return ResponseEntity(DeepLinkingResponse(jwt), HttpStatus.OK)
    }
}
