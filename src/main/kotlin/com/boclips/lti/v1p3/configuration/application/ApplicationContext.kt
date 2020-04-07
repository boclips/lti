package com.boclips.lti.v1p3.configuration.application

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
    fun performSecurityChecks(
        csrfService: CsrfService,
        jwtService: JwtService,
        nonceService: NonceService,
        idTokenValidator: IdTokenValidator
    ) = PerformSecurityChecks(csrfService, jwtService, nonceService, idTokenValidator)

    @Bean
    fun handlePlatformMessage(handleResourceLinkMessage: HandleResourceLinkMessage): HandlePlatformMessage {
        return HandlePlatformMessage(handleResourceLinkMessage)
    }

    @Bean
    fun handleResourceLinkMessage(platformRepository: PlatformRepository) =
        HandleResourceLinkMessage(platformRepository)

    @Bean
    fun idTokenValidator(
        @Value("\${boclips.lti.v1p3.maxTokenAgeInSeconds}") maxTokenAgeInSeconds: String
    ) = IdTokenValidator(maxTokenAgeInSeconds = maxTokenAgeInSeconds.toLong())
}
