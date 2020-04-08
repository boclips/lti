package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwk.SigningKeyNotFoundException
import com.boclips.lti.v1p3.infrastructure.exception.JwksUnreachableException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.security.PublicKey

@Component
class Auth0UrlJwkProviderRetrier {
    @Retryable(
        include = [SigningKeyNotFoundException::class],
        maxAttempts = 2,
        backoff = Backoff(delay = 1000)
    )
    @Throws(SigningKeyNotFoundException::class)
    fun withRetries(fetchAction: () -> PublicKey): PublicKey = fetchAction()

    @Recover
    fun withRetriesRecoveryMethod(e: Exception): PublicKey {
        if (e is SigningKeyNotFoundException) {
            throw JwksUnreachableException(e)
        } else {
            throw e
        }
    }
}
