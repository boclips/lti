package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.domain.model.CollectionRequest
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.presentation.service.ToVideoMetadata
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
// TODO We don't need to map this to LTI 1.1
@RequestMapping("/v1p1")
class CollectionViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val collectionRepository: CollectionRepository,
    private val toVideoMetadata: ToVideoMetadata
) {
    @GetMapping("/collections/{collectionId}")
    fun getCollection(session: HttpSession, @PathVariable("collectionId") collectionId: String): ModelAndView {
        assertHasValidSession(session)

        val collection = collectionRepository.get(
            CollectionRequest(
                collectionId = collectionId,
                integrationId = session.getAttribute(consumerKey) as String
            )
        )

        return ModelAndView(
            "collection", mapOf(
                "customLogoUrl" to session.getAttribute(customLogo),
                "collectionTitle" to collection.title,
                "videos" to collection.videos.map { video -> toVideoMetadata(video) }
            )
        )
    }
}
