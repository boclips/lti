package com.boclips.lti.core.application.service

import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Service
import org.springframework.web.context.annotation.RequestScope
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest

@Service
@RequestScope
class UriComponentsBuilderFactory(private val request: HttpServletRequest) {
    fun getInstance() = UriComponentsBuilder.fromHttpRequest(
        ServletServerHttpRequest(request)
    )
}
