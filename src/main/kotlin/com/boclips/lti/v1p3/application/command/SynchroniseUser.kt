package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.infrastructure.service.IntegrationsClientFactory
import org.springframework.stereotype.Component

@Component
class SynchroniseUser(
    private val integrationsClientFactory: IntegrationsClientFactory
) {
    operator fun invoke(integrationId: String, username: String, deploymentId: String): String {
        return integrationsClientFactory
            .getClient(integrationId = integrationId)
            .synchroniseUser(deploymentId = deploymentId, externalUserId = username).userId
    }
}
