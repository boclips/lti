package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class StatesDoNotMatchException : BoclipsApiException(
    ExceptionDetails(
        error = "Verification failed",
        message = "state verification failed",
        status = HttpStatus.UNAUTHORIZED
    )
)
