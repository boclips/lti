package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.v1p3.application.command.HandlePlatformMessage
import com.boclips.lti.v1p3.application.command.HandleResourceLinkMessage
import com.boclips.lti.v1p3.application.service.CsrfService
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("onePointThreeApplicationContext")
class ApplicationContext {
    @Bean
    fun securityService() = CsrfService()

    @Bean
    fun handlePlatformMessage(handleResourceLinkMessage: HandleResourceLinkMessage): HandlePlatformMessage {
        return HandlePlatformMessage(handleResourceLinkMessage)
    }

    @Bean
    fun handleResourceLinkMessage(platformRepository: PlatformRepository) =
        HandleResourceLinkMessage(platformRepository)
}
