package com.boclips.lti.v1p3.domain.model

import java.net.URL

data class DeepLinkingMessage(
    val issuer: URL,
    val returnUrl: URL,
    val data: String?,
    val subject: String?,
    val deploymentId: String,
    val targetUri: String?,
)
