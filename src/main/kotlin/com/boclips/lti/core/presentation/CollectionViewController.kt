package com.boclips.lti.core.presentation

import com.boclips.lti.v1p1.domain.model.CollectionRequest
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.domain.service.AssertHasLtiSession
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.consumerKeyHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.customLogoHolder
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
    private val assertHasLtiSession: AssertHasLtiSession,
    private val collectionRepository: CollectionRepository,
    private val toVideoMetadata: ToVideoMetadata
) {
    @GetMapping("/collections/{collectionId}")
    fun getCollection(session: HttpSession, @PathVariable("collectionId") collectionId: String): ModelAndView {
        assertHasLtiSession(session)

        val collection = collectionRepository.get(
            CollectionRequest(
                collectionId = collectionId,
                integrationId = session.getAttribute(consumerKeyHolder) as String
            )
        )

        return ModelAndView(
            "collection", mapOf(
                "customLogoUrl" to session.getAttribute(customLogoHolder),
                "collectionTitle" to collection.title,
                "videos" to collection.videos.map { video -> toVideoMetadata(video) }
            )
        )
    }
}
