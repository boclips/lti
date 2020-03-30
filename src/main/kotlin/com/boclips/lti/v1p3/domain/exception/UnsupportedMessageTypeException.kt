package com.boclips.lti.v1p3.domain.exception

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

class UnsupportedMessageTypeException(givenMessageType: String) : BoclipsApiException(
    ExceptionDetails(
        error = "Invalid message type",
        message = "$givenMessageType is not a valid LTI 1.3 message type",
        status = HttpStatus.BAD_REQUEST
    )
)

