package com.boclips.lti.v1p1.configuration.application

import com.boclips.lti.v1p1.domain.service.AssertLaunchRequestIsValid
import com.boclips.lti.v1p1.domain.service.InitializeLtiSession
import com.boclips.lti.v1p1.domain.service.RedirectToRequestedResource
import com.boclips.lti.v1p1.infrastructure.repository.LtiOnePointOneConsumerRepository
import com.boclips.lti.v1p1.infrastructure.service.MongoLtiKeySecretService
import org.imsglobal.aspect.LtiKeySecretService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainContext(private val ltiOnePointOneConsumerRepository: LtiOnePointOneConsumerRepository) {
    @Bean
    fun assertLaunchRequestIsValid() = AssertLaunchRequestIsValid()

    @Bean
    fun initializeLtiSession() = InitializeLtiSession()

    @Bean
    fun redirectToRequestedResource() = RedirectToRequestedResource()

    @Bean
    fun ltiKeySecretService(): LtiKeySecretService {
        return MongoLtiKeySecretService(ltiOnePointOneConsumerRepository)
    }
}
