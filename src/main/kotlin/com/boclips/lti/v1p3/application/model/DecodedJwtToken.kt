package com.boclips.lti.v1p3.application.model

data class DecodedJwtToken(
    val issuerClaim: String?,
    val audienceClaim: List<String?>?,
    val authorizedPartyClaim: String?,
    val expClaim: Long?,
    val issuedAtClaim: Long?,
    val nonceClaim: String?,
    val deploymentIdClaim: String?,
    val messageTypeClaim: String?,
    val targetLinkUriClaim: String?,
    val ltiVersionClaim: String?,
    val resourceLinkClaim: ResourceLinkClaim?,
    val deepLinkingSettingsClaim: DeepLinkingSettingsClaim?,
    val subjectClaim: String?
)
