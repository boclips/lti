package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.core.domain.service.ResourceLinkService
import com.boclips.lti.core.infrastructure.service.IntegrationsClientFactory
import com.boclips.lti.core.infrastructure.service.UsersClientFactory
import com.boclips.lti.v1p3.domain.service.HandleDeepLinkingMessage
import com.boclips.lti.v1p3.domain.service.HandleResourceLinkMessage
import com.boclips.lti.v1p3.domain.service.SynchroniseUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("ltiOnePointThreeDomainContext")
class DomainContext {
    @Bean
    fun handleResourceLinkMessage(
        linkService: ResourceLinkService,
        usersClientFactory: UsersClientFactory
    ) = HandleResourceLinkMessage(linkService, usersClientFactory)

    @Bean
    fun synchroniseUser(
        integrationsClientFactory: IntegrationsClientFactory
    ) = SynchroniseUser(integrationsClientFactory = integrationsClientFactory)

    @Bean
    fun handleDeepLinkingMessage(
        resourceLinkService: ResourceLinkService
    ) = HandleDeepLinkingMessage(resourceLinkService)
}
