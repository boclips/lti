package com.boclips.api.lti.configuration

import org.imsglobal.aspect.LtiKeySecretService
import org.imsglobal.aspect.LtiLaunchVerifier
import org.imsglobal.lti.launch.LtiOauthVerifier
import org.imsglobal.lti.launch.LtiVerifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class LtiContext {
    companion object ConfigurationProperties {
        const val CONSUMER_KEY = "mock-consumer-key"
        const val CONSUMER_SECRET = "mock-consumer-secret"
        const val LANDING_PAGE = "http://localhost/landing-page"
        const val ERROR_PAGE = "http://localhost/error-page"
    }

    @Bean
    fun ltiVerifier(): LtiVerifier {
        return LtiOauthVerifier()
    }

    @Bean
    fun ltiAspect(ltiKeyService: LtiKeySecretService, ltiVerifier: LtiVerifier): LtiLaunchVerifier {
        return LtiLaunchVerifier(ltiKeyService, ltiVerifier)
    }
}
