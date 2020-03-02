package com.boclips.lti.v1p1.presentation

import com.boclips.lti.v1p1.domain.model.VideoRequest
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.lti.v1p1.domain.service.AssertHasLtiSession
import com.boclips.lti.v1p1.domain.service.AssertLaunchRequestIsValid
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.customLogoHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.userIdHolder
import com.boclips.lti.v1p1.domain.service.RedirectToRequestedResource
import com.boclips.lti.v1p1.presentation.service.SortByCollectionTitle
import com.boclips.lti.v1p1.presentation.service.ToCollectionMetadata
import com.boclips.lti.v1p1.presentation.service.ToVideoMetadata
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
    private val initializeLtiSession: InitializeLtiSession,
    private val redirectToRequestedResource: RedirectToRequestedResource,
    private val videoRepository: VideoRepository,
    private val collectionRepository: CollectionRepository,
    private val toVideoMetadata: ToVideoMetadata,
    private val toCollectionMetadata: ToCollectionMetadata,
    private val sortByCollectionTitle: SortByCollectionTitle
) {
    companion object : KLogging()

    @Lti
    @PostMapping(
        "/videos/*",
        "/collections",
        "/collections/*"
    )
    fun handleLtiLaunchRequest(
        request: HttpServletRequest,
        result: LtiVerificationResult,
        session: HttpSession
    ): ResponseEntity<Unit> {
        logger.info { "Received launch request: ${request.method} ${request.requestURL}" }

        assertLaunchRequestIsValid(result)
        initializeLtiSession(request, session)

        return redirectToRequestedResource(request)
    }

    @GetMapping("/videos/{videoId}")
    fun getVideo(session: HttpSession, @PathVariable("videoId") videoId: String): ModelAndView {
        assertHasLtiSession(session)

        return ModelAndView(
            "video", mapOf(
                "customLogoUrl" to session.getAttribute(customLogoHolder),
                "video" to toVideoMetadata(
                    videoRepository.get(VideoRequest(videoId))
                ),
                "userId" to session.getAttribute(userIdHolder)
            )
        )
    }

    @GetMapping("/collections/{collectionId}")
    fun getCollection(session: HttpSession, @PathVariable("collectionId") collectionId: String): ModelAndView {
        assertHasLtiSession(session)

        val collection = collectionRepository.get(collectionId)

        return ModelAndView(
            "collection", mapOf(
                "customLogoUrl" to session.getAttribute(customLogoHolder),
                "collectionTitle" to collection.title,
                "videos" to collection.videos.map { video -> toVideoMetadata(video) }
            )
        )
    }

    @GetMapping("/collections")
    fun getUserCollections(session: HttpSession): ModelAndView {
        assertHasLtiSession(session)

        val userCollections = collectionRepository.getMyCollections()

        return ModelAndView(
            "userCollections", mapOf(
                "customLogoUrl" to session.getAttribute(customLogoHolder),
                "collections" to sortByCollectionTitle(
                    userCollections.map { collection -> toCollectionMetadata(collection) }
                )
            )
        )
    }
}
