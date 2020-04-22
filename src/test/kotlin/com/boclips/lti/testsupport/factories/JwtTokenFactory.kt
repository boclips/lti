package com.boclips.lti.testsupport.factories

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.util.Date
import java.util.UUID

object JwtTokenFactory {
    fun sample(
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
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/message_type", "LtiResourceLinkRequest")
            .withClaim("https://purl.imsglobal.org/spec/lti/claim/version", "1.3.0")
            .withClaim(
                "https://purl.imsglobal.org/spec/lti/claim/resource_link",
                mapOf("id" to "test-resource-link-id")
            )
            .sign(signatureAlgorithm)
    }
}
