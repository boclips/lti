package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.model.DecodedJwtToken

object DecodedJwtTokenFactory {
    fun sample(
        issuerClaim: String? = "https://lms.com",
        targetLinkUriClaim: String? = "https://tool.com/resource/1",
        messageTypeClaim: String? = "LtiResourceLinkRequest",
        ltiVersionClaim: String? = "1.3.0"
    ) = DecodedJwtToken(
        issuerClaim = issuerClaim,
        targetLinkUriClaim = targetLinkUriClaim,
        messageTypeClaim = messageTypeClaim,
        ltiVersionClaim = ltiVersionClaim
    )
}
