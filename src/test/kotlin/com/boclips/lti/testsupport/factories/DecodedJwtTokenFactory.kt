package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.model.DecodedJwtToken

object DecodedJwtTokenFactory {
    fun sample(
        issuer: String? = "https://lms.com",
        targetLinkUri: String? = "https://tool.com/resource/1",
        messageType: String? = "LtiResourceLinkRequest"
    ) = DecodedJwtToken(
        issuer = issuer,
        targetLinkUri = targetLinkUri,
        messageType = messageType
    )
}
