package com.boclips.lti.v1p1.application.service

import com.boclips.lti.v1p1.application.model.AccessTokenResponse
import com.boclips.lti.v1p1.configuration.properties.VideoServiceProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class VideoServiceAccessTokenProvider(
    private val videoServiceProperties: VideoServiceProperties,
    private val restTemplateWithVideoServiceAuth: RestTemplate
) {
    fun getAccessToken(): String {
        val requestEntity = HttpEntity(
            "grant_type=client_credentials",
            HttpHeaders().apply {
                contentType = MediaType.APPLICATION_FORM_URLENCODED
            }
        )

        return restTemplateWithVideoServiceAuth.exchange(
            videoServiceProperties.accessTokenUri,
            HttpMethod.POST,
            requestEntity,
            AccessTokenResponse::class.java
        ).body!!.access_token
    }
}
