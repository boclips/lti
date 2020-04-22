package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.model.ResourceLinkClaim
import java.time.Instant.now
import java.util.UUID

object DecodedJwtTokenFactory {
    fun sample(
        issuerClaim: String? = "https://lms.com",
        audienceClaim: List<String>? = listOf("boclips"),
        authorizedPartyClaim: String? = "0b300627-a87a-4b15-b748-ae1aa18ef3eb",
        expClaim: Long? = now().plusSeconds(10).epochSecond,
        issuedAtClaim: Long? = now().minusSeconds(10).epochSecond,
        nonceClaim: String? = UUID.randomUUID().toString(),
        deploymentIdClaim: String? = UUID.randomUUID().toString(),
        targetLinkUriClaim: String? = "https://tool.com/resource/1",
        messageTypeClaim: String? = "LtiResourceLinkRequest",
        ltiVersionClaim: String? = "1.3.0",
        resourceLinkClaim: ResourceLinkClaim? = sampleResourceLinkClaim()
    ) = DecodedJwtToken(
        issuerClaim = issuerClaim,
        audienceClaim = audienceClaim,
        authorizedPartyClaim = authorizedPartyClaim,
        expClaim = expClaim,
        issuedAtClaim = issuedAtClaim,
        deploymentIdClaim = deploymentIdClaim,
        targetLinkUriClaim = targetLinkUriClaim,
        messageTypeClaim = messageTypeClaim,
        ltiVersionClaim = ltiVersionClaim,
        resourceLinkClaim = resourceLinkClaim,
        nonceClaim = nonceClaim
    )

    fun sampleResourceLinkClaim(id: String? = UUID.randomUUID().toString()) = ResourceLinkClaim(id)
}
