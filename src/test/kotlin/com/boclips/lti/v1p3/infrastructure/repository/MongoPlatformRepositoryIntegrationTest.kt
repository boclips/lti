package com.boclips.lti.v1p3.infrastructure.repository

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p3.domain.exception.PlatformNotFoundException
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

class MongoPlatformRepositoryIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    private lateinit var platformRepository: PlatformRepository

    @Test
    fun `can retrieve a platform by issuer`() {
        val issuer = URL("https://lms.com/its-great")
        val authenticationEndpoint = URL("https://idp.lms.com/auth")
        mongoPlatformDocumentRepository.insert(
            PlatformDocument(
                id = ObjectId(),
                issuer = issuer.toString(),
                authenticationEndpoint = authenticationEndpoint.toString()
            )
        )

        val platform = platformRepository.getByIssuer(issuer)

        assertThat(platform.issuer).isEqualTo(issuer)
        assertThat(platform.authenticationEndpoint).isEqualTo(authenticationEndpoint)
    }

    @Test
    fun `throws a PlatformNotFoundException when document is not found by issuer`() {
        val ninjaIssuer = "https://this.does/not/exist"

        assertThatThrownBy { platformRepository.getByIssuer(URL(ninjaIssuer)) }
            .isInstanceOf(PlatformNotFoundException::class.java)
            .hasMessageContaining(ninjaIssuer)
    }
}
