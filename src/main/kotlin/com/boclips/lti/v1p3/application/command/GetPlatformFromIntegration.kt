package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.infrastructure.repository.MongoIntegrationDocumentRepository
import com.boclips.lti.v1p3.application.exception.IntegrationNotFoundException
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.domain.model.Platform
import com.boclips.lti.v1p3.domain.repository.PlatformRepository

class GetPlatformFromIntegration(
    private val integrationDocumentRepository: MongoIntegrationDocumentRepository,
    private val platformRepository: PlatformRepository
) {
    operator fun invoke(integrationId: String): Platform {
        val integration =
            integrationDocumentRepository.findOneByIntegrationId(integrationId) ?: throw IntegrationNotFoundException(
                integrationId
            )

        return try {
            platformRepository.getByClientId(integration.clientId)
        } catch (e: PlatformNotFoundException) {
            throw com.boclips.lti.v1p3.application.exception.PlatformNotFoundException(integration.clientId)
        }
    }
}
