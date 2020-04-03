package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.model.getTargetLinkUri
import com.boclips.lti.v1p3.application.model.setIntegrationId
import com.boclips.lti.v1p3.domain.exception.ResourceDoesNotMatchException
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL
import javax.servlet.http.HttpSession

class HandleResourceLinkMessage(
    private val platformRepository: PlatformRepository
) {
    operator fun invoke(message: ResourceLinkMessage, session: HttpSession): URL {
        assertRequestedResourceMatchesOriginalLoginRequest(message.requestedResource, session)

        val platform = platformRepository.getByIssuer(message.issuer)
        session.setIntegrationId(platform.issuer.toString())

        return message.requestedResource
    }

    private fun assertRequestedResourceMatchesOriginalLoginRequest(requestedResource: URL, session: HttpSession) {
        if (requestedResource != URL(session.getTargetLinkUri())) throw ResourceDoesNotMatchException()
    }
}
