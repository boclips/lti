package com.boclips.lti.v1p3.domain.model

import java.net.URL

data class ResourceLinkMessage(val issuer: URL, val requestedResource: URL, val subject: String? = null)
