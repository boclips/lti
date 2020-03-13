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

            // Core LTI features
            .antMatchers(HttpMethod.GET, "/videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/collections").permitAll()
            .antMatchers(HttpMethod.GET, "/collections/*").permitAll()
            .antMatchers(HttpMethod.GET, "/auth/token").permitAll()

            // TODO There will be a transition period where we support both paths to not break
            // existing user sessions.
            .antMatchers(HttpMethod.GET, "/v1p1/videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/v1p1/collections").permitAll()
            .antMatchers(HttpMethod.GET, "/v1p1/collections/*").permitAll()

            // LTI 1.1
            .antMatchers(HttpMethod.POST, "/v1p1/**").permitAll()

            .anyRequest().denyAll()
    }
}
