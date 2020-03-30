package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.application.model.DecodedJwtToken

interface JwtService {
    fun isSignatureValid(token: String): Boolean
    fun decode(token: String): DecodedJwtToken
}
