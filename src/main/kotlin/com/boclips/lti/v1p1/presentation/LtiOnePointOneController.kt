package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.application.service.VideoUrlFor
import com.boclips.lti.v1p1.domain.service.AssertHasLtiSession
import com.boclips.lti.v1p1.domain.service.AssertLaunchRequestIsValid
import com.boclips.lti.v1p1.domain.service.RedirectToRequestedResource
import com.boclips.lti.v1p1.presentation.model.VideoMetadata
import com.boclips.videos.service.client.VideoServiceClient
import mu.KLogging
import org.imsglobal.aspect.Lti
import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/v1p1")
class LtiOnePointOneController(
    val assertLaunchRequestIsValid: AssertLaunchRequestIsValid,
    val assertHasLtiSession: AssertHasLtiSession,
    val redirectToRequestedResource: RedirectToRequestedResource,
    val videoUrlFor: VideoUrlFor,
    val videoServiceClient: VideoServiceClient
) {
    companion object : KLogging() {
        const val authenticationStateHolder = "isAuthenticated"
    }

    @Lti
    @PostMapping(
        "/videos/{resourceId}",
        "/collections/{resourceId}"
    )
    fun handleLtiLaunchRequest(
        request: HttpServletRequest,
        result: LtiVerificationResult,
        session: HttpSession,
        @PathVariable("resourceId") resourceId: String
    ): ResponseEntity<Unit> {
        logger.info { "Received request: ${request.method} ${request.requestURL}" }

        assertLaunchRequestIsValid(result)
        session.setAttribute(authenticationStateHolder, true)
        return redirectToRequestedResource(request)
    }

    @GetMapping("/videos/{videoId}")
    fun getVideo(session: HttpSession, @PathVariable("videoId") videoId: String): ModelAndView {
        assertHasLtiSession(session)

        return ModelAndView(
            "video", mapOf(
                "videoUrl" to videoUrlFor(videoId)
            )
        )
    }

    @GetMapping("/collections/{collectionId}")
    fun getCollection(session: HttpSession, @PathVariable("collectionId") collectionId: String): ModelAndView {
        assertHasLtiSession(session)

        val collection = videoServiceClient.get(videoServiceClient.rawIdToCollectionId(collectionId))

        return ModelAndView(
            "collection", mapOf(
                "videos" to collection.videos.map {
                    VideoMetadata(it.value, "", "", "")
                }
            )
        )
    }
}
