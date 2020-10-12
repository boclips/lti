package com.boclips.lti.v1p3.configuration.application

import com.boclips.lti.v1p3.application.command.PerformSecurityChecks
import com.boclips.lti.v1p3.application.command.SetUpSession
import com.boclips.lti.v1p3.application.interceptor.SecurityChecksInterceptor
import com.boclips.lti.v1p3.application.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.handler.MappedInterceptor

@Configuration
 class WebConfiguration(
    // private val performSecurityChecks: PerformSecurityChecks,
    // private val jwtService: JwtService,
    // private val setUpSession: SetUpSession
): WebMvcConfigurer {

    // override fun addInterceptors(registry: InterceptorRegistry) {
    //     registry.addInterceptor(securityChecksInterceptor(performSecurityChecks, jwtService, setUpSession))
    // }

    //creating interceptor as spring bean does the same as adding it to registry (commented out above)
    //so creating this WebConfiguration is not really necessary but keeping interceptors in a separate config might make things tidier
    @Bean
    fun securityChecksInterceptor(
        performSecurityChecks: PerformSecurityChecks,
        jwtService: JwtService,
        setUpSession: SetUpSession
    ): MappedInterceptor = MappedInterceptor(
            arrayOf("/v1p3/authentication-response"),
            SecurityChecksInterceptor(performSecurityChecks, jwtService, setUpSession)
    )
}
