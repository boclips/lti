package com.boclips.lti.v1p1.application.service

import com.boclips.lti.v1p1.application.model.AccessTokenResponse
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import com.boclips.lti.v1p1.infrastructure.service.KeycloakClientFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service
class VideoServiceAccessTokenProvider(
    private val videoServiceProperties: VideoServiceProperties,
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
            videoServiceProperties.tokenUrl,
            HttpMethod.POST,
            requestEntity,
            AccessTokenResponse::class.java
        ).body!!.access_token
    }
}
