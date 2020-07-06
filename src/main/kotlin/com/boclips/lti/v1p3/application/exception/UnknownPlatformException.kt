package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class UnknownPlatformException(issuerString: String) : BoclipsApiException(
    exceptionDetails = ExceptionDetails(
        error = "Unauthorised",
        message = "Unknown platform: $issuerString",
        status = HttpStatus.UNAUTHORIZED
    )
)

