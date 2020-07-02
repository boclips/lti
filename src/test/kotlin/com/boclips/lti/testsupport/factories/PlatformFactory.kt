package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.domain.model.Platform
import java.net.URL
import java.util.UUID

object PlatformFactory {
    fun sample(issuer: URL = URL("https://lms.com"), clientId: String = UUID.randomUUID().toString()): Platform =
        Platform(
            issuer = issuer,
            clientId = clientId,
            authenticationEndpoint = URL("https://lms.com/auth"),
            jwksEndpoint = URL("https://lms.com/.well-known/jwks")
        )
}
