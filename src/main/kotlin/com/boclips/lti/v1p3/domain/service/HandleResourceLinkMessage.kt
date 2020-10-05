package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.infrastructure.service.UsersClientFactory
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import com.boclips.lti.v1p3.domain.model.setIntegrationId
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL
import javax.servlet.http.HttpSession

class HandleResourceLinkMessage(
    private val platformRepository: PlatformRepository,
    private val linkService: ResourceLinkService,
    private val usersClientFactory: UsersClientFactory
) {
    operator fun invoke(message: ResourceLinkMessage, session: HttpSession): URL {
        val platform = platformRepository.getByIssuer(message.issuer)
        val integrationId = platform.issuer.toString()
        session.setIntegrationId(integrationId)

        return if (message.requestedResource.isSearchResourceRequest()) {
            val showCopyLink = isCopyResourceLinkFeatureAvailable(integrationId)
            linkService.getSearchVideoLink(showCopyLink)
        } else {
            message.requestedResource
        }
    }

    private fun isCopyResourceLinkFeatureAvailable(integrationId: String) =
        usersClientFactory
            .getClient(integrationId)
            .getLoggedInUser()
            .features?.get("LTI_COPY_RESOURCE_LINK") ?: false
}

fun URL.isSearchResourceRequest() = this.toString().endsWith("search")
