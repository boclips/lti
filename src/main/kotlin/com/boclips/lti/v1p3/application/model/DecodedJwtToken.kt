package com.boclips.lti.v1p3.application.model

data class DecodedJwtToken(val issuerClaim: String?, val targetLinkUriClaim: String?, val messageTypeClaim: String?)
