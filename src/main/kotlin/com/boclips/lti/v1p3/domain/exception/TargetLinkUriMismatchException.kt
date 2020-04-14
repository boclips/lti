package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class TargetLinkUriMismatchException : BoclipsApiException(
    ExceptionDetails(
        error = "Unauthorised",
        message = "Requested and expected resource does not match",
        status = HttpStatus.UNAUTHORIZED
    )
)
