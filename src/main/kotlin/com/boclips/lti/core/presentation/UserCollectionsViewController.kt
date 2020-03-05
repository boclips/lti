package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.v1p1.domain.model.CollectionsRequest
import com.boclips.lti.v1p1.domain.repository.CollectionRepository
import com.boclips.lti.v1p1.presentation.service.SortByCollectionTitle
import com.boclips.lti.v1p1.presentation.service.ToCollectionMetadata
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
// TODO We don't need to map this to LTI 1.1
@RequestMapping("/v1p1")
class UserCollectionsViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val collectionRepository: CollectionRepository,
    private val toCollectionMetadata: ToCollectionMetadata,
    private val sortByCollectionTitle: SortByCollectionTitle
) {
    @GetMapping("/collections")
    fun getUserCollections(session: HttpSession): ModelAndView {
        assertHasValidSession(session)

        val userCollections = collectionRepository.getMyCollections(
            CollectionsRequest(
                integrationId = session.getAttribute(consumerKey) as String
            )
        )

        return ModelAndView(
            "userCollections", mapOf(
                "customLogoUrl" to session.getAttribute(customLogo),
                "collections" to sortByCollectionTitle(
                    userCollections.map { collection -> toCollectionMetadata(collection) }
                )
            )
        )
    }
}
