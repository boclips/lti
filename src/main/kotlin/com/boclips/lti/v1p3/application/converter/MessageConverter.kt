package com.boclips.lti.v1p3.application.converter

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import java.net.URL

object MessageConverter {
    fun toResourceLinkMessage(token: DecodedJwtToken): ResourceLinkMessage = ResourceLinkMessage(
        issuer = URL(token.issuerClaim),
        subject = token.subjectClaim,
        requestedResource = URL(token.targetLinkUriClaim)
    )

    fun toDeepLinkingMessage(token: DecodedJwtToken): DeepLinkingMessage = DeepLinkingMessage(
        issuer = URL(token.issuerClaim),
        returnUrl = URL(token.deepLinkingSettingsClaim!!.deepLinkReturnUrl),
        data = token.deepLinkingSettingsClaim.data,
        subject = token.subjectClaim,
        deploymentId = token.deploymentIdClaim!!
    )
}
