package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class JwtClaimValidationException(message: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Token claims validation failed",
        message = message,
        status = HttpStatus.UNAUTHORIZED
    )
)
