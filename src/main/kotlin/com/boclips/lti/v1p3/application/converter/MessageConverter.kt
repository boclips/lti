package com.boclips.lti.v1p3.application.converter

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import java.net.URL

object MessageConverter {
    fun toResourceLinkMessage(token: DecodedJwtToken): ResourceLinkMessage = ResourceLinkMessage(
        issuer = URL(token.issuerClaim),
        requestedResource = URL(token.targetLinkUriClaim)
    )
}
