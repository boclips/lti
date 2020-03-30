package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class InvalidIdTokenSignatureException : BoclipsApiException(
    exceptionDetails = ExceptionDetails(
        error = "Unauthorised",
        message = "Token signature was invalid",
        status = HttpStatus.UNAUTHORIZED
    )
)
