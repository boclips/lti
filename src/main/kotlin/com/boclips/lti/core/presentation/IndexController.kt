package com.boclips.lti.core.presentation

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class IndexController() {
    @GetMapping("/")
    fun getIndex(): ModelAndView {
        return ModelAndView(
            "index", mapOf("message" to "HELLO WORLD")
        )
    }
}
