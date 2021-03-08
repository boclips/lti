package com.boclips.lti.v1p3.application.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class StatesDoNotMatchException(stateValue: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Unauthorised",
        message = "'$stateValue' not found in the session as a state assigned at login initiation",
        status = HttpStatus.UNAUTHORIZED
    )
)
