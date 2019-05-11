package com.boclips.api.lti.presentation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/v1")
class LTIController {
    @GetMapping("", "/")
    fun sayHelloWorld(request: HttpServletRequest, response: HttpServletResponse): String {
        return "Hello, World!"
    }
}
