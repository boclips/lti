package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.interfaces.RSAKeyProvider
import java.security.interfaces.RSAPublicKey

class Auth0JwksKeyProvider(private val jwksProvider: JwkProvider) : RSAKeyProvider {
    override fun getPrivateKeyId() =
        throw UnsupportedOperationException("This provider supports signature verification only")

    override fun getPrivateKey() =
        throw UnsupportedOperationException("This provider supports signature verification only")

    override fun getPublicKeyById(keyId: String?): RSAPublicKey {
        return jwksProvider.get(keyId).publicKey as RSAPublicKey
    }
}
