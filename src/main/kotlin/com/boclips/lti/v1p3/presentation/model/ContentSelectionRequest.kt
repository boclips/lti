package com.boclips.lti.v1p3.presentation.model

data class ContentSelectionRequest(
    val selectedItems: List<SelectedVideoRequest>,
    val data: String,
    val deploymentId: String
)
