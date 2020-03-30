package com.boclips.lti.v1p3.application.model

data class DecodedJwtToken(val issuer: String?, val targetLinkUri: String?, val messageType: String?)
