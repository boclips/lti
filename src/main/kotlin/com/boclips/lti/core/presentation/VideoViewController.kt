package com.boclips.lti.core.presentation

import com.boclips.lti.core.application.model.SessionKeys.consumerKey
import com.boclips.lti.core.application.model.SessionKeys.customLogo
import com.boclips.lti.core.application.model.SessionKeys.userId
import com.boclips.lti.core.application.service.AssertHasValidSession
import com.boclips.lti.core.domain.model.VideoRequest
import com.boclips.lti.core.domain.repository.VideoRepository
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
class VideoViewController(
    private val assertHasValidSession: AssertHasValidSession,
    private val videoRepository: VideoRepository,
    private val toVideoMetadata: ToVideoMetadata
) {
    @GetMapping("/videos/{videoId}")
    fun getVideo(session: HttpSession, @PathVariable("videoId") videoId: String): ModelAndView {
        assertHasValidSession(session)

        return ModelAndView(
            "video", mapOf(
                "customLogoUrl" to session.getAttribute(customLogo),
                "video" to toVideoMetadata(
                    videoRepository.get(
                        VideoRequest(
                            videoId = videoId,
                            integrationId = session.getAttribute(consumerKey) as String
                        )
                    )
                ),
                "userId" to session.getAttribute(userId)
            )
        )
    }
}
