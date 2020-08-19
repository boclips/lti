package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.application.model.SessionKeys
import com.boclips.lti.v1p3.application.converter.MessageConverter
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.model.setIntegrationId
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import com.boclips.lti.v1p3.domain.model.setUserId
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL
import javax.servlet.http.HttpSession

class HandleResourceLinkMessage(
    private val platformRepository: PlatformRepository
) {
    operator fun invoke(message: ResourceLinkMessage, session: HttpSession, userId: String? = null): URL {
        val platform = platformRepository.getByIssuer(message.issuer)
        session.setIntegrationId(platform.issuer.toString())
        if (!userId.isNullOrEmpty()) {
            session.setUserId(userId)
        }

        return message.requestedResource
    }
}
