package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.application.service.VideoUrlFor
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.domain.service.AssertHasLtiSession
import com.boclips.lti.v1p1.domain.service.AssertLaunchRequestIsValid
import com.boclips.lti.v1p1.domain.service.FindVideoMetadata
import com.boclips.lti.v1p1.domain.service.RedirectToRequestedResource
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
    private val assertLaunchRequestIsValid: AssertLaunchRequestIsValid,
    private val assertHasLtiSession: AssertHasLtiSession,
    private val redirectToRequestedResource: RedirectToRequestedResource,
    private val videoUrlFor: VideoUrlFor,
    private val collectionRepository: CollectionRepository,
    private val findVideoMetadata: FindVideoMetadata
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

        val collection = collectionRepository.get(collectionId)

        return ModelAndView(
            "collection", mapOf(
                "videos" to collection.videos.mapNotNull { videoId -> findVideoMetadata(videoId) }
            )
        )
    }
}
