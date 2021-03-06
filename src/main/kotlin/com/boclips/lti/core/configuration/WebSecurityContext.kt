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
            .antMatchers(HttpMethod.GET, "/actuator/prometheus").permitAll()

            // Assets
            .antMatchers(HttpMethod.GET, "/styles/*.css").permitAll()
            .antMatchers(HttpMethod.GET, "/*.js").permitAll()
            .antMatchers(HttpMethod.GET, "/*.js.*").permitAll()
            .antMatchers(HttpMethod.GET, "/*.css").permitAll()
            .antMatchers(HttpMethod.GET, "/*.css.*").permitAll()

            .antMatchers(HttpMethod.GET, "/static/**").permitAll()
            .antMatchers(HttpMethod.GET, "/manifest.json").permitAll()
            .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
            .antMatchers(HttpMethod.GET, "/robots.txt").permitAll()
            .antMatchers(HttpMethod.GET, "/*.png").permitAll()

            // Exposed resources
            .antMatchers(HttpMethod.GET, "/videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/collections").permitAll()
            .antMatchers(HttpMethod.GET, "/collections/*").permitAll()
            .antMatchers(HttpMethod.GET, "/search").permitAll()
            .antMatchers(HttpMethod.GET, "/search-and-embed").permitAll()
            .antMatchers(HttpMethod.GET, "/embeddable-videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/auth/token").permitAll()

            // LTI 1.1 Launch endpoint
            .antMatchers(HttpMethod.POST, "/v1p1/**").permitAll()

            // LTI 1.3 Login endpoints
            .antMatchers(HttpMethod.POST, "/v1p3/initiate-login").permitAll()
            .antMatchers(HttpMethod.GET, "/v1p3/initiate-login").permitAll()
            .antMatchers(HttpMethod.POST, "/v1p3/authentication-response").permitAll()

            // LTI 1.3 Deep Linking Response endpoint
            .antMatchers(HttpMethod.POST, "/v1p3/deep-linking-response").permitAll()

            // JWKS endpoint
            .antMatchers(HttpMethod.GET, "/.well-known/jwks").permitAll()

            // Dev support
            .antMatchers(HttpMethod.POST, "/dev-support/initialise-session").permitAll()

            .anyRequest().denyAll()
    }
}
