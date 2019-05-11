package com.boclips.api.lti.presentation

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class LTIController {
    @GetMapping("", "/")
    fun sayHelloWorld(serverHttpRequest: ServerHttpRequest, response: ServerHttpResponse): String {
        return "Hello, World!"
    }
}
