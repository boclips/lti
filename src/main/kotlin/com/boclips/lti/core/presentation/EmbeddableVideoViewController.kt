package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.application.service.LtiSessionHelpers
import com.boclips.lti.core.domain.model.VideoQuery
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.presentation.service.ToVideoViewModel
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
class EmbeddableVideoViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val videoRepository: VideoRepository,
    private val toVideoViewModel: ToVideoViewModel
) {
    @GetMapping("/embeddable-videos/{id}")
    fun getEmbeddableVideo(session: HttpSession, @PathVariable("id") videoId: String): ModelAndView {
        assertHasValidSession(session)

        val video = videoRepository.get(
            VideoQuery(
                videoId = videoId,
                integrationId = LtiSessionHelpers.getIntegrationId(session)
            )
        )

        return ModelAndView(
            "embeddable-video", mapOf(
                "video" to toVideoViewModel(video),
                "userId" to session.getAttribute(SessionKeys.boclipsUserId)
            )
        )
    }
}
