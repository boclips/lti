package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.infrastructure.service.IntegrationsClientFactory

class SynchroniseUser(
    private val integrationsClientFactory: IntegrationsClientFactory
) {
    operator fun invoke(integrationId: String, externalUserId: String, deploymentId: String): String {
        return integrationsClientFactory
            .getClient(integrationId = integrationId)
            .synchroniseUser(deploymentId = deploymentId, externalUserId = externalUserId).userId
    }
}
