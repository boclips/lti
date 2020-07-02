package com.boclips.lti.v1p3.application.service

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.model.DeepLinkingSelection
import com.boclips.lti.v1p3.domain.model.Platform

interface JwtService {
    fun isSignatureValid(token: String): Boolean
    fun decode(token: String): DecodedJwtToken
    fun createDeepLinkingResponseToken(platform: Platform, deepLinkingSelection: DeepLinkingSelection): String
}
