package com.boclips.lti.v1p3.application.command

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.v1p3.application.exception.UnknownPlatformException
import com.boclips.users.api.factories.UserResourceFactory
import com.boclips.users.api.httpclient.test.fakes.UsersClientFake
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

class SynchroniseUserTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var synchroniseUser: SynchroniseUser

    @Test
    fun `throws when integration id is not a valid issuer URL`() {
        assertThrows<UnknownPlatformException> {
            synchroniseUser("this is not a URL")
        }
    }

    @Test
    fun `returns given user's boclips user id`() {
        val issuer = URL("https://platform.com/")

        val userClient: UsersClientFake = usersClientFactory.getClient(issuer.toString()) as UsersClientFake
        val newUser = UserResourceFactory.sample(
            id = "1",
            firstName = "Baptiste",
            features = mapOf(
                "LTI_COPY_RESOURCE_LINK" to true
            )
        )
        userClient.add(newUser)
        userClient.setLoggedInUser(newUser)

        integrationDocumentRepository.insert(
            IntegrationDocument(
                id = ObjectId(),
                integrationId = "a-splendid-lti-integration",
                clientId = "id",
                clientSecret = "secret"
            )
        )

        val user = synchroniseUser(
            integrationId = "a-splendid-lti-integration",
            username = "Test-user-name",
            deploymentId = "deployment-id"
        )

        assertThat(user).isEqualTo("user-id")
    }
}
