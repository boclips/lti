package com.boclips.lti.testsupport.factories

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.boclips.lti.v1p3.domain.model.MessageTypes
import java.time.Instant
import java.util.Date
import java.util.UUID

object JwtTokenFactory {
    fun sampleResourceLinkRequestJwt(
        issuer: String = "https://lms.com",
        audience: List<String> = listOf("some audience"),
        targetLinkUri: String = "https://tool.com/resource/123",
        signatureAlgorithm: Algorithm
    ): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(*audience.toTypedArray())
            .withExpiresAt(Date.from(Instant.now().plusSeconds(10)))
            .withIssuedAt(Date.from(Instant.now().minusSeconds(10)))
            .withClaim("nonce", UUID.randomUUID().toString())
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id", "test-deployment-id")
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri", targetLinkUri)
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/message_type", MessageTypes.ResourceLinkRequest)
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/version", "1.3.0")
            .withClaim(
                "https://purl.imsglobal.org/spec/lti/claim/resource_link",
                mapOf("id" to "test-resource-link-id")
            )
            .sign(signatureAlgorithm)
    }

    fun sampleDeepLinkingRequestJwt(
        issuer: String = "https://lms.com",
        audience: List<String> = listOf("some audience"),
        signatureAlgorithm: Algorithm
    ): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(*audience.toTypedArray())
            .withExpiresAt(Date.from(Instant.now().plusSeconds(10)))
            .withIssuedAt(Date.from(Instant.now().minusSeconds(10)))
            .withClaim("nonce", UUID.randomUUID().toString())
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id", "test-deployment-id")
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/message_type", MessageTypes.DeepLinkingRequest)
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/version", "1.3.0")
            .withClaim(
                "https://purl.imsglobal.org/spec/lti-dl/claim/deep_linking_settings",
                mapOf(
                    "deep_link_return_url" to "https://lms.com/return-here-please",
                    "accept_types" to listOf("ltiResourceLink"),
                    "accept_presentation_document_targets" to emptyList<String>() // We don't really care about this value now
                )
            )
            .sign(signatureAlgorithm)
    }
}
