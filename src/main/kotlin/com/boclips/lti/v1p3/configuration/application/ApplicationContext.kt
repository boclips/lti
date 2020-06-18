package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.v1p3.application.command.AssembleLoginRequestUrl
import com.boclips.lti.v1p3.application.command.HandleDeepLinkingMessage
import com.boclips.lti.v1p3.application.command.HandlePlatformMessage
import com.boclips.lti.v1p3.application.command.HandleResourceLinkMessage
import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.service.CsrfService
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.NonceService
import com.boclips.lti.v1p3.application.validator.IdTokenValidator
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
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
        uriComponentsBuilderFactory: UriComponentsBuilderFactory
    ) = AssembleLoginRequestUrl(platformRepository, uriComponentsBuilderFactory)

    @Bean
    fun performSecurityChecks(
        csrfService: CsrfService,
        jwtService: JwtService,
        nonceService: NonceService,
        idTokenValidator: IdTokenValidator
    ) = PerformSecurityChecks(csrfService, jwtService, nonceService, idTokenValidator)

    @Bean
    fun handlePlatformMessage(
        handleResourceLinkMessage: HandleResourceLinkMessage,
        handleDeepLinkingMessage: HandleDeepLinkingMessage
    ): HandlePlatformMessage {
        return HandlePlatformMessage(handleResourceLinkMessage, handleDeepLinkingMessage)
    }

    @Bean
    fun handleResourceLinkMessage(platformRepository: PlatformRepository) =
        HandleResourceLinkMessage(platformRepository)

    @Bean
    fun handleDeepLinkingMessage(
        platformRepository: PlatformRepository,
        uriComponentsBuilderFactory: UriComponentsBuilderFactory
    ) =
        HandleDeepLinkingMessage(platformRepository, uriComponentsBuilderFactory)

    @Bean
    fun idTokenValidator(
        platformRepository: PlatformRepository,
        @Value("\${boclips.lti.v1p3.maxTokenAgeInSeconds}") maxTokenAgeInSeconds: String
    ) = IdTokenValidator(platformRepository = platformRepository, maxTokenAgeInSeconds = maxTokenAgeInSeconds.toLong())
}
