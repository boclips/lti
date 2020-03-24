package com.boclips.lti.core.configuration

import com.boclips.security.HttpSecurityConfigurer
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Component
class WebSecurityContext : HttpSecurityConfigurer {
    override fun configure(http: HttpSecurity) {
        http
            .headers()
            .frameOptions().disable()

            .and()
            .authorizeRequests()
            
            // Infrastructure
            .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()

            // Assets
            .antMatchers(HttpMethod.GET, "/styles/*.css").permitAll()

            // Core LTI features
            .antMatchers(HttpMethod.GET, "/videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/collections").permitAll()
            .antMatchers(HttpMethod.GET, "/collections/*").permitAll()
            .antMatchers(HttpMethod.GET, "/auth/token").permitAll()

            // LTI 1.1
            .antMatchers(HttpMethod.POST, "/v1p1/**").permitAll()

            // LTI 1.3
            .antMatchers(HttpMethod.POST, "/v1p3/initiate-login").permitAll()

            .anyRequest().denyAll()
    }
}
