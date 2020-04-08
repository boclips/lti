package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.interfaces.RSAKeyProvider
import java.security.interfaces.RSAPublicKey

class Auth0JwksKeyProvider(
    private val jwksProvider: UrlJwkProvider,
    private val retrier: Auth0UrlJwkProviderRetrier
) : RSAKeyProvider {
    override fun getPrivateKeyId() =
        throw UnsupportedOperationException("This provider supports signature verification only")

    override fun getPrivateKey() =
        throw UnsupportedOperationException("This provider supports signature verification only")

    override fun getPublicKeyById(keyId: String?): RSAPublicKey {
        return retrier.withRetries { jwksProvider.get(keyId).publicKey } as RSAPublicKey
    }
}
