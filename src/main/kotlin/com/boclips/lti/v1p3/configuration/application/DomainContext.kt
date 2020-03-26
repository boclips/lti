package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.v1p3.domain.service.VerifyCrossSiteRequestForgeryProtection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("onePointThreeDomainContext")
class DomainContext {
    @Bean
    fun assertVerificationResponseIsValid() = VerifyCrossSiteRequestForgeryProtection()
}
