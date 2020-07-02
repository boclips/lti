package com.boclips.lti.v1p3.application.model

data class DeepLinkingSelection(val deploymentId: String, val data: String, val selectedVideos: List<SelectedVideo>)
