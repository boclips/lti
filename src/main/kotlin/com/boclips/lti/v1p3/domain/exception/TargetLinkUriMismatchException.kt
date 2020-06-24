package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class TargetLinkUriMismatchException : BoclipsApiException(
    ExceptionDetails(
        error = "Unauthorised",
        message = "Requested resource does not match target_link_uri value requested on login",
        status = HttpStatus.UNAUTHORIZED
    )
)
