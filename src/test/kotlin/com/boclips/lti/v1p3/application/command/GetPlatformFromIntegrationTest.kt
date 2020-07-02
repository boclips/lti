package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p3.application.exception.IntegrationNotFoundException
import com.boclips.lti.v1p3.application.exception.PlatformNotFoundException
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class GetPlatformFromIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getPlatformFromIntegration: GetPlatformFromIntegration

    @Test
    fun `throws when invalid integration`() {
        assertThrows<IntegrationNotFoundException> {
            getPlatformFromIntegration("missing")
        }
    }

    @Test
    fun `throws when invalid platform`() {
        integrationDocumentRepository.save(
            IntegrationDocument(
                id = ObjectId(),
                integrationId = "123",
                clientId = "client-id",
                clientSecret = "client-secret"
            )
        )

        assertThrows<PlatformNotFoundException> { getPlatformFromIntegration("123") }
    }
}
