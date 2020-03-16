package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.application.service.LtiSessionHelpers.getIntegrationId
import com.boclips.lti.core.domain.model.CollectionRequest
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.presentation.service.ToVideoViewModel
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
class CollectionViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val collectionRepository: CollectionRepository,
    private val toVideoViewModel: ToVideoViewModel
) {
    @GetMapping(
        // TODO There will be a transition period where we support both paths to not break
        // existing user sessions.
        "/v1p1/collections/{collectionId}",
        "/collections/{collectionId}"
    )
    fun getCollection(session: HttpSession, @PathVariable("collectionId") collectionId: String): ModelAndView {
        assertHasValidSession(session)

        val collection = collectionRepository.get(
            CollectionRequest(
                collectionId = collectionId,
                integrationId = getIntegrationId(session)
            )
        )

        return ModelAndView(
            "collection", mapOf(
                "customLogoUrl" to session.getAttribute(customLogo),
                "collectionTitle" to collection.title,
                "videos" to collection.videos.map { video -> toVideoViewModel(video) }
            )
        )
    }
}
