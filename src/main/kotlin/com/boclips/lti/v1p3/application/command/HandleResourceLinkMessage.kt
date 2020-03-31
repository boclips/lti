package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.service.LtiOnePointThreeSession
import com.boclips.lti.v1p3.domain.exception.ResourceDoesNotMatchException
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL

class HandleResourceLinkMessage(
    private val session: LtiOnePointThreeSession,
    private val platformRepository: PlatformRepository
) {
    operator fun invoke(message: ResourceLinkMessage): URL {
        if (URL(session.getTargetLinkUri()) != message.requestedResource) throw ResourceDoesNotMatchException()

        val platform = platformRepository.getByIssuer(message.issuer)
        session.setIntegrationId(platform.issuer.toString())

        return message.requestedResource
    }
}
