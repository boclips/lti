package com.boclips.lti.v1p3.application.model

data class DeepLinkingSettingsClaim(
    val deepLinkReturnUrl: String?,
    val acceptTypes: List<String>?,
    val acceptPresentationDocumentTargets: List<String>?,
    val data: String?
)
