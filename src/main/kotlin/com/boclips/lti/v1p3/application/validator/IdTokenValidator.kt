package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.JwtClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import java.time.Instant
import java.time.Instant.now

object IdTokenValidator {
    const val clientId = "boclips"

    fun assertHasValidClaims(token: DecodedJwtToken) {
        if (token.issuerClaim.isNullOrBlank()) throw JwtClaimValidationException("'iss' was not provided")

        token.audienceClaim?.find { it == clientId }
            ?: throw JwtClaimValidationException("'aud' does not contain a valid value")
        if (token.audienceClaim.size > 1) {
            if (token.authorizedPartyClaim != clientId) throw JwtClaimValidationException("'azp' does not contain a valid value")
        } else {
            if (token.authorizedPartyClaim != null && token.authorizedPartyClaim != clientId) throw JwtClaimValidationException(
                "'azp' does not contain a valid value"
            )
        }

        token.expClaim?.let {
            val expiryTimestamp = Instant.ofEpochSecond(it)
            if (now().isAfter(expiryTimestamp)) throw JwtClaimValidationException("'exp' indicates this token has expired")
        }

        if (token.nonceClaim.isNullOrBlank()) throw JwtClaimValidationException("'nonce' was not provided")
    }
}
