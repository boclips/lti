package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.JwtClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL
import java.time.Instant
import java.time.Instant.now

class IdTokenValidator(
    private val platformRepository: PlatformRepository,
    private val maxTokenAgeInSeconds: Long,
    private val currentTime: () -> Instant = ::now
) {
    fun assertHasValidClaims(token: DecodedJwtToken) {
        if (token.issuerClaim.isNullOrBlank()) throw JwtClaimValidationException("'iss' was not provided")
        assertHasValidAudience(token)
        assertIsWithinTimeConstraints(token)
        if (token.nonceClaim.isNullOrBlank()) throw JwtClaimValidationException("'nonce' was not provided")
    }

    private fun assertIsWithinTimeConstraints(token: DecodedJwtToken) {
        token.expClaim?.let {
            val expiryTimestamp = Instant.ofEpochSecond(it)
            if (currentTime().isAfter(expiryTimestamp)) throw JwtClaimValidationException("'exp' indicates this token has expired")
        } ?: throw JwtClaimValidationException("'exp' claim was not provided")

        token.issuedAtClaim?.let {
            val issueTimestamp = Instant.ofEpochSecond(it)
            if (currentTime().isBefore(issueTimestamp)) throw JwtClaimValidationException("'iat' is in the future")

            val tokenAgeThreshold = currentTime().minusSeconds(maxTokenAgeInSeconds)
            if (issueTimestamp.isBefore(tokenAgeThreshold)) throw JwtClaimValidationException("'iat' is too far in the past")
        } ?: throw JwtClaimValidationException("'iat' claim was not provided")
    }

    private fun assertHasValidAudience(token: DecodedJwtToken) {
        val platform = platformRepository.getByIssuer(URL(token.issuerClaim))

        val aud = token.audienceClaim
        val azp = token.authorizedPartyClaim
        when {
            aud == null -> throw JwtClaimValidationException("No 'aud' provided")
            aud.contains(platform.clientId) -> return
            aud.size == 1 && azp == platform.clientId -> return
            else -> throw JwtClaimValidationException(
                "Either 'aud' should contain '${platform.clientId}' or 'aud' should have one value and 'azp' should be '${platform.clientId}'"
            )
        }
    }
}
