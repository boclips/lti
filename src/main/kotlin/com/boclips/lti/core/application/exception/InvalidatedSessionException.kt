package com.boclips.lti.core.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidatedSessionException(message: String) : Exception(message)
