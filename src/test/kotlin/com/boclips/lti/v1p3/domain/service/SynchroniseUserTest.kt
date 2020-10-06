package com.boclips.lti.v1p3.domain.service

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.users.api.httpclient.test.fakes.IntegrationsClientFake
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.net.URL

internal class SynchroniseUserTest : AbstractSpringIntegrationTest() {
    @Autowired
    lateinit var synchroniseUser: SynchroniseUser

    @Test
    fun `returns given user's boclips user id`() {
        val issuer = URL("https://platform.com/")

        val integrationsClient: IntegrationsClientFake = integrationsClientFactory.getClient(issuer.toString()) as IntegrationsClientFake
        integrationsClient.add("deployment-id" to "Test-user-name")

        val user = synchroniseUser(
            integrationId = "a-splendid-lti-integration",
            externalUserId = "Test-user-name",
            deploymentId = "deployment-id"
        )

        assertThat(user).isNotNull
    }

    @Test
    fun `returns the same user id when called twice`() {
        val issuer = URL("https://platform.com/")

        val integrationsClient: IntegrationsClientFake = integrationsClientFactory.getClient(issuer.toString()) as IntegrationsClientFake
        integrationsClient.add("deployment-id" to "Test-user-name")

        val user = synchroniseUser(
            integrationId = "a-splendid-lti-integration",
            externalUserId = "Test-user-name",
            deploymentId = "deployment-id"
        )
        val user1 = synchroniseUser(
            integrationId = "a-splendid-lti-integration",
            externalUserId = "Test-user-name",
            deploymentId = "deployment-id"
        )

        assertThat(user).isNotNull
        assertThat(user).isEqualTo(user1)
    }

    @Test
    fun `users with the same external user IDs but different deployment IDs return different user ids`() {
        val issuer = URL("https://platform.com/")

        val integrationsClient: IntegrationsClientFake = integrationsClientFactory.getClient(issuer.toString()) as IntegrationsClientFake
        integrationsClient.add("deployment-id" to "Test-user-name")
        integrationsClient.add("deployment-id-1" to "Test-user-name")

        val user = synchroniseUser(
            integrationId = "a-splendid-lti-integration",
            externalUserId = "Test-user-name",
            deploymentId = "deployment-id"
        )
        val user1 = synchroniseUser(
            integrationId = "a-splendid-lti-integration",
            externalUserId = "Test-user-name",
            deploymentId = "deployment-id-1"
        )

        assertThat(user).isNotNull
        assertThat(user).isNotEqualTo(user1)
    }
}
