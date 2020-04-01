package com.boclips.lti.v1p3.infrastructure.service

import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.model.ResourceLinkClaim
import com.boclips.lti.v1p3.application.service.JwtService
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import java.net.URL

class Auth0JwtService(private val platformRepository: PlatformRepository) : JwtService {
    override fun isSignatureValid(token: String): Boolean {
        val decodedToken = JWT.decode(token)
        val platform = platformRepository.getByIssuer(URL(decodedToken.issuer))

        val keyProvider = Auth0JwksKeyProvider(UrlJwkProvider(platform.jwksEndpoint))
        val algorithm = Algorithm.RSA256(keyProvider)

        return try {
            algorithm.verify(decodedToken)
            true
        } catch (e: SignatureVerificationException) {
            false
        }
    }

    override fun decode(token: String) = JWT.decode(token)
        .let {
            DecodedJwtToken(
                issuerClaim = it.issuer,
                targetLinkUriClaim = it.getClaim("https://purl.imsglobal.org/spec/lti/claim/target_link_uri")
                    .asString(),
                deploymentIdClaim = it.getClaim("https://purl.imsglobal.org/spec/lti/claim/deployment_id").asString(),
                messageTypeClaim = it.getClaim("https://purl.imsglobal.org/spec/lti/claim/message_type").asString(),
                ltiVersionClaim = it.getClaim("https://purl.imsglobal.org/spec/lti/claim/version").asString(),
                resourceLinkClaim = it.getClaim("https://purl.imsglobal.org/spec/lti/claim/resource_link").asMap()
                    ?.let { claim -> ResourceLinkClaim(claim["id"].toString()) }
            )
        }
}
