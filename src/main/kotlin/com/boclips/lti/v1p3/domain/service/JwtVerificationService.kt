package com.boclips.lti.v1p3.domain.service

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.boclips.lti.v1p3.application.service.JwksKeyProvider
import com.boclips.lti.v1p3.domain.repository.PlatformRepository

class JwtVerificationService(private val platformRepository: PlatformRepository) {
    fun verifySignature(token: String): Boolean {
        val decodedToken = JWT.decode(token)
        val platform = platformRepository.getByIssuer(java.net.URL(decodedToken.issuer))

        val keyProvider = JwksKeyProvider(UrlJwkProvider(platform.jwksEndpoint))
        val algorithm = Algorithm.RSA256(keyProvider)

        return try {
            algorithm.verify(decodedToken)
            true
        } catch (e: SignatureVerificationException) {
            false
        }
    }
}
