package com.boclips.lti.v1p3.presentation

import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.v1p3.application.command.GetPlatformFromIntegration
import com.boclips.lti.v1p3.application.exception.IntegrationNotFoundException
import com.boclips.lti.v1p3.application.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.application.model.DeepLinkingSelection
import com.boclips.lti.v1p3.application.model.SelectedVideo
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
import java.net.URL
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/v1p3/deep-linking-response")
class DeepLinkingResponseController(
    private val assertHasValidSession: AssertHasValidSession,
    private val jwtService: JwtService,
    private val getPlatformFromIntegration: GetPlatformFromIntegration
) {
    @PostMapping
    fun generateDeepLinkingResponseForSelection(
        @RequestBody contentSelection: ContentSelectionRequest,
        httpSession: HttpSession
    ): ResponseEntity<DeepLinkingResponse> {
        assertHasValidSession(httpSession)

        val platform = try {
            getPlatformFromIntegration(integrationId = httpSession.getIntegrationId())
        } catch (e: IntegrationNotFoundException) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        } catch (e: PlatformNotFoundException) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val jwt = jwtService.createDeepLinkingResponseToken(
            platform, DeepLinkingSelection(
                deploymentId = contentSelection.deploymentId,
                selectedVideos = contentSelection.selectedItems.map { SelectedVideo(url = URL("https://${it.id}.com")) },
                data = contentSelection.data
            )
        )

        return ResponseEntity(DeepLinkingResponse(jwt), HttpStatus.OK)
    }
}
