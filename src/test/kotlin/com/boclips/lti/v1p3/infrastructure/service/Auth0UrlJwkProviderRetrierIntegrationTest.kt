package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwk.SigningKeyNotFoundException
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p3.infrastructure.exception.JwksUnreachableException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import java.security.PublicKey

@ExtendWith(MockitoExtension::class)
class Auth0UrlJwkProviderRetrierIntegrationTest : AbstractSpringIntegrationTest() {
    @Test
    fun `forwards the returned result`(@Mock key: PublicKey) {
        val fetchAction = { key }

        val result = retrier.withRetries(fetchAction)

        assertThat(result).isSameAs(key)
    }

    @Test
    fun `retries once for SigningKeyNotFoundException and forwards the returned result`(@Mock key: PublicKey) {
        var numberOfCalls = 0
        val fetchAction = {
            numberOfCalls += 1
            if (numberOfCalls < 2) {
                throw SigningKeyNotFoundException("Uh-oh", RuntimeException("This is really bad"))
            }
            key
        }

        val result = retrier.withRetries(fetchAction)

        assertThat(result).isSameAs(key)
        assertThat(numberOfCalls).isEqualTo(2)
    }

    @Test
    fun `fails with our exception when it's unable to retrieve the key on second attempt`() {
        val fetchAction = {
            throw SigningKeyNotFoundException("Uh-oh", RuntimeException("This is really bad"))
        }

        assertThrows<JwksUnreachableException> { retrier.withRetries(fetchAction) }
    }

    @Test
    fun `forwards other kinds of exceptions without retrying`() {
        var numberOfCalls = 0
        val fetchAction = {
            numberOfCalls += 1
            throw IllegalStateException("Wow, what's happening now?")
        }

        assertThrows<IllegalStateException> { retrier.withRetries(fetchAction) }
        assertThat(numberOfCalls).isEqualTo(1)
    }
}
