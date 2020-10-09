package com.boclips.lti.testsupport.configuration

import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This is a nasty hack to stop spring session repository filter creating a new session by default
 * We want to keep the session we set up in the tests
 */
@Component
@Profile("test")
@Order(Integer.MIN_VALUE)
class DisableSessionRepositoryFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.session != null) {
            request.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", true);
        }

        filterChain.doFilter(request, response)
    }
}
