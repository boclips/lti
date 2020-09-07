package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.infrastructure.service.UsersClientFactory
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.domain.service.HandleDeepLinkingMessage
import com.boclips.lti.v1p3.domain.service.HandleResourceLinkMessage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("ltiOnePointThreeDomainContext")
class DomainContext {
    @Bean
    fun handleResourceLinkMessage(
        platformRepository: PlatformRepository,
        linkService: ResourceLinkService,
        usersClientFactory: UsersClientFactory
    ) =
        HandleResourceLinkMessage(platformRepository, linkService, usersClientFactory)

    @Bean
    fun handleDeepLinkingMessage(
        platformRepository: PlatformRepository,
        resourceLinkService: ResourceLinkService
    ) = HandleDeepLinkingMessage(
            platformRepository,
            resourceLinkService
        )
}
