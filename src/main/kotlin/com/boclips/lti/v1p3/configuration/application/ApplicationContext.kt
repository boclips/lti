package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.core.domain.repository.VideoRepository
import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.application.command.*
import com.boclips.lti.v1p3.application.converter.RsaKeyPairConverter
import com.boclips.lti.v1p3.application.service.CsrfService
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.KeyPairService
import com.boclips.lti.v1p3.application.service.NonceService
import com.boclips.lti.v1p3.application.validator.DeepLinkingRequestValidator
import com.boclips.lti.v1p3.application.validator.IdTokenValidator
import com.boclips.lti.v1p3.configuration.properties.SigningKeysProperties
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.domain.service.HandleDeepLinkingMessage
import com.boclips.lti.v1p3.domain.service.HandleResourceLinkMessage
import com.boclips.lti.v1p3.domain.service.SynchroniseUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("onePointThreeApplicationContext")
class ApplicationContext {
    @Bean
    fun securityService() = CsrfService()

    @Bean
    fun assembleLoginRequestUrl(
        platformRepository: PlatformRepository,
        resourceLinkService: ResourceLinkService
    ) = AssembleLoginRequestUrl(platformRepository, resourceLinkService)

    @Bean
    fun performSecurityChecks(
        csrfService: CsrfService,
        jwtService: JwtService,
        nonceService: NonceService,
        idTokenValidator: IdTokenValidator
    ) = PerformSecurityChecks(csrfService, jwtService, nonceService, idTokenValidator)

    @Bean
    fun handlePlatformRequest(
        handleResourceLinkRequest: HandleResourceLinkRequest,
        handleDeepLinkingRequest: HandleDeepLinkingRequest
    ): HandlePlatformRequest {
        return HandlePlatformRequest(handleResourceLinkRequest, handleDeepLinkingRequest)
    }

    @Bean
    fun handleResourceLinkRequest(handleResourceLinkMessage: HandleResourceLinkMessage) =
        HandleResourceLinkRequest(handleResourceLinkMessage)

    @Bean
    fun handleDeepLinkingRequest(
        deepLinkingRequestValidator: DeepLinkingRequestValidator,
        handleDeepLinkingMessage: HandleDeepLinkingMessage
    ) = HandleDeepLinkingRequest(deepLinkingRequestValidator, handleDeepLinkingMessage)

    @Bean
    fun idTokenValidator(
        platformRepository: PlatformRepository,
        @Value("\${boclips.lti.v1p3.maxTokenAgeInSeconds}") maxTokenAgeInSeconds: String
    ) = IdTokenValidator(platformRepository = platformRepository, maxTokenAgeInSeconds = maxTokenAgeInSeconds.toLong())

    @Bean
    fun deepLinkingRequestValidator(resourceLinkService: ResourceLinkService) =
        DeepLinkingRequestValidator(resourceLinkService)

    @Bean
    fun getPlatformFromIntegration(
        platformRepository: PlatformRepository
    ) = GetPlatformForIntegration(platformRepository)

    @Bean
    fun getSelectedItems(resourceLinkService: ResourceLinkService, videoRepository: VideoRepository) =
        GetSelectedItems(videoRepository = videoRepository, resourceLinkService = resourceLinkService)

    @Bean
    fun keyPairService(signingKeysProperties: SigningKeysProperties) = KeyPairService(
        keyPairs = signingKeysProperties.signingKeys.map(RsaKeyPairConverter::toSigningKeyPair)
    )

    @Bean
    fun setUpSession(
        synchroniseUser: SynchroniseUser,
        platformRepository: PlatformRepository
    ) = SetUpSession(
        synchroniseUser = synchroniseUser,
        platformRepository = platformRepository
    )
}
