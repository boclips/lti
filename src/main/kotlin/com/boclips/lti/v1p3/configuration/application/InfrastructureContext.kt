package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoPlatformDocumentRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoPlatformRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("onePointThreeInfrastructureContext")
class InfrastructureContext {
    @Bean
    fun platformRepository(mongoPlatformDocumentRepository: MongoPlatformDocumentRepository): PlatformRepository {
        return MongoPlatformRepository(mongoPlatformDocumentRepository)
    }
}
