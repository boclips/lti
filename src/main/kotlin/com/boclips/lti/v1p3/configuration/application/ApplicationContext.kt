package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.v1p3.application.service.SecurityService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("onePointThreeApplicationContext")
class ApplicationContext {
    @Bean
    fun securityService() = SecurityService()
}
