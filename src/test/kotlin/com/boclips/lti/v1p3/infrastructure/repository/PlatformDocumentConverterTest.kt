package com.boclips.lti.v1p3.infrastructure.repository

import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.MalformedURLException

class PlatformDocumentConverterTest {
    @Test
    fun `throws an exception when issuer is not a valid URL`() {
        val document = PlatformDocument(
            id = ObjectId(),
            issuer = "trololo",
            authenticationEndpoint = "https://valid.com/auth",
            jwksUrl = "https://valid.com/.well-known/jwks.json"
        )

        assertThrows<MalformedURLException> { PlatformDocumentConverter.toDomainInstance(document) }
    }

    @Test
    fun `throws an exception when authentication endpoint is not a valid URL`() {
        val document = PlatformDocument(
            id = ObjectId(),
            issuer = "https://platform.com/lms",
            authenticationEndpoint = "whoa",
            jwksUrl = "https://valid.com/.well-known/jwks.json"
        )

        assertThrows<MalformedURLException> { PlatformDocumentConverter.toDomainInstance(document) }
    }

    @Test
    fun `throws an exception when jwks endpoint is not a valid URL`() {
        val document = PlatformDocument(
            id = ObjectId(),
            issuer = "https://platform.com/lms",
            authenticationEndpoint = "https://valid.com/auth",
            jwksUrl = "hey ho"
        )

        assertThrows<MalformedURLException> { PlatformDocumentConverter.toDomainInstance(document) }
    }
}
