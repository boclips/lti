package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.PlatformDocumentFactory
import com.boclips.lti.v1p3.application.exception.UnknownPlatformException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class GetPlatformForIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var getPlatformForIntegration: GetPlatformForIntegration

    @Test
    fun `throws when integration id is not a valid issuer URL`() {
        assertThrows<UnknownPlatformException> {
            getPlatformForIntegration("this is not a URL")
        }
    }

    @Test
    fun `throws when platform is not found for given integration id`() {
        assertThrows<UnknownPlatformException> {
            getPlatformForIntegration("https://platform.com")
        }
    }

    @Test
    fun `returns given platform`() {
        val issuer = "https://platform.com"
        mongoPlatformDocumentRepository.insert(PlatformDocumentFactory.sample(issuer = issuer))

        val platform = getPlatformForIntegration(issuer)

        assertThat(platform.issuer.toString()).isEqualTo(issuer)
    }
}
