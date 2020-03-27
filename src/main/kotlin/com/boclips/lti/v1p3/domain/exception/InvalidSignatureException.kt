package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class InvalidSignatureException : BoclipsApiException(
    exceptionDetails = ExceptionDetails(
        error = "Unauthorised",
        message = "Could not verify signature of token",
        status = HttpStatus.UNAUTHORIZED
    )
)
