package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class UnsupportedSigningAlgorithmException(algorithm: String) : BoclipsApiException(
    exceptionDetails = ExceptionDetails(
        error = "Unauthorised",
        message = "$algorithm is not supported for JWT signing/verification",
        status = HttpStatus.UNAUTHORIZED
    )
)
