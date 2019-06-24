package com.boclips.lti.v1p1.application.service

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest

@Service
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UriComponentsBuilderFactory(val request: HttpServletRequest) {
    fun getInstance() = UriComponentsBuilder.fromHttpRequest(
        ServletServerHttpRequest(request)
    )
}
