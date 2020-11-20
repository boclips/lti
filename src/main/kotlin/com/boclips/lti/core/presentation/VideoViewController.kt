package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.application.service.LtiSessionHelpers.getIntegrationId
import com.boclips.lti.core.domain.model.VideoQuery
import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.presentation.service.ToVideoViewModel
import com.boclips.lti.v1p3.domain.model.getBoclipsUserId
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpSession

@Controller
class VideoViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val videoRepository: VideoRepository,
    private val toVideoViewModel: ToVideoViewModel
) {
    @GetMapping("/videos/{videoId}")
    fun getVideo(session: HttpSession, @PathVariable("videoId") videoId: String): ModelAndView {
        assertHasValidSession(session)

        val video = videoRepository.get(
            VideoQuery(
                videoId = videoId,
                integrationId = getIntegrationId(session)
            )
        )

        return ModelAndView(
            "video", mapOf(
                "customLogoUrl" to session.getAttribute(customLogo),
                "video" to toVideoViewModel(video),
                "userId" to session.getBoclipsUserId()
            )
        )
    }
}
