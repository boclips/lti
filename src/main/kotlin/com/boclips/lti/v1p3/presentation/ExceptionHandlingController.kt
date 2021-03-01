package com.boclips.lti.v1p3.presentation

import com.boclips.web.exceptions.BoclipsApiException
import mu.KLogging
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ExceptionHandlingController {
    companion object : KLogging()

    @ExceptionHandler(BoclipsApiException::class)
    fun handleBoclipsExceptions(req: HttpServletRequest, ex: BoclipsApiException): ModelAndView {
        val message = formatBoclipsApiExceptionDetails(ex)
        logger.info { message }

        val mav = ModelAndView()
        mav.status = (ex.exceptionDetails.status)
        mav.addObject("message", message)
        mav.addObject("url", req.requestURL)
        mav.viewName = "error"

        return mav
    }
}
