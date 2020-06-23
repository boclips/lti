package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.v1p3.application.command.AssembleLoginRequestUrl
import com.boclips.lti.v1p3.application.command.HandleDeepLinkingRequest
import com.boclips.lti.v1p3.application.command.HandlePlatformRequest
import com.boclips.lti.v1p3.application.command.HandleResourceLinkRequest
import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.service.CsrfService
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.application.service.NonceService
import com.boclips.lti.v1p3.application.validator.IdTokenValidator
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.domain.service.HandleDeepLinkingMessage
import com.boclips.lti.v1p3.domain.service.HandleResourceLinkMessage
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
    fun handleDeepLinkingRequest(handleDeepLinkingMessage: HandleDeepLinkingMessage) =
        HandleDeepLinkingRequest(handleDeepLinkingMessage)

    @Bean
    fun idTokenValidator(
        platformRepository: PlatformRepository,
        @Value("\${boclips.lti.v1p3.maxTokenAgeInSeconds}") maxTokenAgeInSeconds: String
    ) = IdTokenValidator(platformRepository = platformRepository, maxTokenAgeInSeconds = maxTokenAgeInSeconds.toLong())
}
