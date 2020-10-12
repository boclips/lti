package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class ParamValidationException(
     override val message: String
) : BoclipsApiException(
    exceptionDetails = ExceptionDetails(
        error = "Validation error",
        message = message,
        status = HttpStatus.BAD_REQUEST
    )
)
