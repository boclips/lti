package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class InvalidJwtTokenSignatureException : BoclipsApiException(
    exceptionDetails = ExceptionDetails(
        error = "Unauthorised",
        message = "Token signature was invalid",
        status = HttpStatus.UNAUTHORIZED
    )
)
