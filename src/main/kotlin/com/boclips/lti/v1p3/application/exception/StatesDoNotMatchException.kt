package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class StatesDoNotMatchException : BoclipsApiException(
    ExceptionDetails(
        error = "Unauthorised",
        message = "Given and expected states do not match",
        status = HttpStatus.UNAUTHORIZED
    )
)
