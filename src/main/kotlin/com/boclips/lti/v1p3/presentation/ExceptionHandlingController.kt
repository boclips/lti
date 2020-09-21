package com.boclips.lti.v1p3.presentation

import mu.KLogging
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ExceptionHandlingController {
    companion object : KLogging()

    @ExceptionHandler(Exception::class)
    fun handleError(req: HttpServletRequest, ex: Exception): ModelAndView {
        logger.info{"exception message: " + ex.message}

        val mav = ModelAndView()
        mav.addObject("message", ex.message)
        mav.addObject("url", req.requestURL)
        mav.viewName = "error"

        return mav
    }
}
