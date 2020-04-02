package com.boclips.lti.v1p3.application.model

data class DecodedJwtToken(
    val issuerClaim: String?,
    val deploymentIdClaim: String?,
    val targetLinkUriClaim: String?,
    val messageTypeClaim: String?,
    val ltiVersionClaim: String?,
    val resourceLinkClaim: ResourceLinkClaim?,
    val nonceClaim: String?
)
