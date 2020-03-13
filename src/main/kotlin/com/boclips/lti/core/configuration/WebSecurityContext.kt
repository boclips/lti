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
            .antMatcher("/**")
            .authorizeRequests()
            // Infrastructure
            .antMatchers(HttpMethod.GET, "/actuator/health").permitAll()
            // Core LTI features
            .antMatchers(HttpMethod.GET, "/videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/collections").permitAll()
            .antMatchers(HttpMethod.GET, "/collections/*").permitAll()
            // LTI 1.1
            .antMatchers(HttpMethod.POST, "/v1p1/**").permitAll()
    }
}
