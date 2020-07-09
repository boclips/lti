package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.model.DeepLinkingSettingsClaim
import com.boclips.lti.v1p3.application.model.ResourceLinkClaim
import com.boclips.lti.v1p3.domain.model.MessageTypes
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
        targetLinkUriClaim: String? = null,
        messageTypeClaim: String? = MessageTypes.ResourceLinkRequest,
        ltiVersionClaim: String? = "1.3.0",
        resourceLinkClaim: ResourceLinkClaim? = sampleResourceLinkClaim(),
        deepLinkingSettingsClaim: DeepLinkingSettingsClaim? = null
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
        nonceClaim = nonceClaim,
        deepLinkingSettingsClaim = deepLinkingSettingsClaim
    )

    fun sampleDeepLinkingToken(
        issuerClaim: String? = "https://lms.com",
        audienceClaim: List<String>? = listOf("boclips"),
        nonceClaim: String? = UUID.randomUUID().toString(),
        deploymentIdClaim: String? = UUID.randomUUID().toString(),
        deepLinkingSettingsClaim: DeepLinkingSettingsClaim? = sampleDeepLinkingSettingsClaim()
    ) = sample(
        issuerClaim = issuerClaim,
        audienceClaim = audienceClaim,
        nonceClaim = nonceClaim,
        deploymentIdClaim = deploymentIdClaim,
        messageTypeClaim = MessageTypes.DeepLinkingRequest,
        deepLinkingSettingsClaim = deepLinkingSettingsClaim
    )

    fun sampleResourceLinkClaim(id: String? = UUID.randomUUID().toString()) = ResourceLinkClaim(id)

    fun sampleDeepLinkingSettingsClaim(
        deepLinkReturnUrl: String? = "https://platform.com/deep-link-return",
        acceptTypes: List<String>? = listOf("ltiResourceLink"),
        acceptPresentationDocumentTargets: List<String>? = emptyList(),
        data: String? = "an opaque value that has to be sent back to the caller"
    ) = DeepLinkingSettingsClaim(
        deepLinkReturnUrl = deepLinkReturnUrl,
        acceptTypes = acceptTypes,
        acceptPresentationDocumentTargets = acceptPresentationDocumentTargets,
        data = data
    )
}
