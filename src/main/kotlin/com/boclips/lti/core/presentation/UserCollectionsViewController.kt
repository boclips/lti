package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.domain.model.CollectionsRequest
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.presentation.service.SortByCollectionTitle
import com.boclips.lti.core.presentation.service.ToCollectionViewModel
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
class UserCollectionsViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val collectionRepository: CollectionRepository,
    private val toCollectionViewModel: ToCollectionViewModel,
    private val sortByCollectionTitle: SortByCollectionTitle
) {
    @GetMapping(
        // TODO There will be a transition period where we support both paths to not break
        // existing user sessions.
        "/v1p1/collections",
        "/collections"
    )
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
                    userCollections.map { collection -> toCollectionViewModel(collection) }
                )
            )
        )
    }
}
