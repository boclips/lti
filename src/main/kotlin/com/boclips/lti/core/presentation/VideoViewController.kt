package com.boclips.lti.core.presentation

import com.boclips.lti.v1p1.domain.model.VideoRequest
import com.boclips.lti.v1p1.domain.repository.VideoRepository
import com.boclips.lti.v1p1.domain.service.AssertHasLtiSession
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.consumerKeyHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.customLogoHolder
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession.Companion.userIdHolder
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
    private val assertHasLtiSession: AssertHasLtiSession,
    private val videoRepository: VideoRepository,
    private val toVideoMetadata: ToVideoMetadata
) {
    @GetMapping("/videos/{videoId}")
    fun getVideo(session: HttpSession, @PathVariable("videoId") videoId: String): ModelAndView {
        assertHasLtiSession(session)

        return ModelAndView(
            "video", mapOf(
                "customLogoUrl" to session.getAttribute(customLogoHolder),
                "video" to toVideoMetadata(
                    videoRepository.get(
                        VideoRequest(
                            videoId = videoId,
                            integrationId = session.getAttribute(consumerKeyHolder) as String
                        )
                    )
                ),
                "userId" to session.getAttribute(userIdHolder)
            )
        )
    }
}
