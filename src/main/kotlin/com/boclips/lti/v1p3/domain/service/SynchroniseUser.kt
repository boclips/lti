package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.core.infrastructure.service.IntegrationsClientFactory
import mu.KLogging

class SynchroniseUser(
    private val integrationsClientFactory: IntegrationsClientFactory
) {
    companion object : KLogging()

    operator fun invoke(integrationId: String, externalUserId: String, deploymentId: String): String {
        return try {
            integrationsClientFactory
                .getClient(integrationId = integrationId)
                .synchroniseUser(deploymentId = deploymentId, externalUserId = externalUserId).userId
        } catch (e: Exception) {
            logger.info(e) {
                "exception when trying to synch user for integrationId: $integrationId," +
                    " deploymentId=$deploymentId, externalUserId: $externalUserId"
            }
            externalUserId
        }
    }
}
