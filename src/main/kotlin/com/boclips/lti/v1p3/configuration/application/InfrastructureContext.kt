package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.KeyPairService
import com.boclips.lti.v1p3.application.service.NonceService
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoNonceDocumentRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoPlatformDocumentRepository
import com.boclips.lti.v1p3.infrastructure.repository.MongoPlatformRepository
import com.boclips.lti.v1p3.infrastructure.service.Auth0JwtService
import com.boclips.lti.v1p3.infrastructure.service.Auth0UrlJwkProviderRetrier
import com.boclips.lti.v1p3.infrastructure.service.MongoNonceService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("onePointThreeInfrastructureContext")
class InfrastructureContext {
    @Bean
    fun platformRepository(mongoPlatformDocumentRepository: MongoPlatformDocumentRepository): PlatformRepository {
        return MongoPlatformRepository(mongoPlatformDocumentRepository)
    }

    @Bean
    fun jwtService(
        platformRepository: PlatformRepository,
        retrier: Auth0UrlJwkProviderRetrier,
        @Value("\${boclips.lti.v1p3.maxTokenAgeInSeconds}") maxTokenAgeInSeconds: String,
        keyPairService: KeyPairService
    ): JwtService = Auth0JwtService(platformRepository, retrier, maxTokenAgeInSeconds.toLong(), keyPairService)

    @Bean
    fun nonceService(
        mongoNonceDocumentRepository: MongoNonceDocumentRepository,
        @Value("\${boclips.lti.v1p3.nonceLifetimeInHours}") nonceLifetimeInHours: String
    ): NonceService =
        MongoNonceService(mongoNonceDocumentRepository, nonceLifetimeInHours.toLong())
}
