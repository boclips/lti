package com.boclips.lti.v1p1.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.BAD_REQUEST)
class LaunchRequestInvalidException(message: String) : RuntimeException(message)
