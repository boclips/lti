package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class NonceReusedException(nonce: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Nonce validation failed",
        message = "Nonce '$nonce' has already been used",
        status = HttpStatus.UNAUTHORIZED
    )
)
