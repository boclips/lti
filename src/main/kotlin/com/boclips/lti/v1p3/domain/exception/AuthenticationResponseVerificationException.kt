package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

abstract class AuthenticationResponseVerificationException(message: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Authentication response verification failed",
        message = message,
        status = HttpStatus.UNAUTHORIZED
    )
)
