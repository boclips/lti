package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.application.service.LtiSessionHelpers.getIntegrationId
import com.boclips.lti.core.domain.model.CollectionsQuery
import com.boclips.lti.core.domain.repository.CollectionRepository
import com.boclips.lti.core.presentation.service.SortByCollectionTitle
import com.boclips.lti.core.presentation.service.ToCollectionViewModel
import org.springframework.http.HttpStatus
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
    @GetMapping("/collections")
    fun getUserCollections(session: HttpSession): ModelAndView {
        try {
            assertHasValidSession(session)
        } catch (e: Exception) {
            return ModelAndView("error/invalidSession", HttpStatus.UNAUTHORIZED)
        }
        val userCollections = collectionRepository.getMyCollections(
            CollectionsQuery(
                integrationId = getIntegrationId(session)
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
