package com.boclips.lti.core.configuration.application

import com.boclips.lti.core.application.service.AssertHasValidSession
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationContext() {
    @Bean
    fun assertHasValidSession() = AssertHasValidSession()
}
