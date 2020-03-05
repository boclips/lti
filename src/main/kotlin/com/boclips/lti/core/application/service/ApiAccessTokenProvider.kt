package com.boclips.lti.core.application.service

import com.boclips.lti.core.application.model.AccessTokenResponse
import com.boclips.lti.core.infrastructure.configuration.properties.BoclipsApiProperties
import com.boclips.lti.core.infrastructure.service.KeycloakClientFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class ApiAccessTokenProvider(
    private val boclipsApiProperties: BoclipsApiProperties,
    private val keycloakClientFactory: KeycloakClientFactory
) {
    fun getAccessToken(integrationId: String): String {
        val requestEntity = HttpEntity(
            "grant_type=client_credentials",
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
            }
        )

        return keycloakClientFactory.getKeycloakClient(integrationId).exchange(
            boclipsApiProperties.tokenUrl,
            HttpMethod.POST,
            requestEntity,
            AccessTokenResponse::class.java
        ).body!!.access_token
    }
}
