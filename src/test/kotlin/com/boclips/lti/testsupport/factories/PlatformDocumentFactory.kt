package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import org.bson.types.ObjectId

object PlatformDocumentFactory {
    fun sample(
        issuer: String = "https://lms.com",
        authenticationEndpoint: String = "https://lms.com/auth",
        jwksUrl: String = "https://lms.com/.well-known/jwks.json",
        clientId: String = "random id"
    ) =
        PlatformDocument(
            id = ObjectId(),
            issuer = issuer,
            authenticationEndpoint = authenticationEndpoint,
            jwksUrl = jwksUrl,
            clientId = clientId
        )
}
