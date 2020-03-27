package com.boclips.lti.v1p3.domain.model

import java.net.URL

data class Platform(val issuer: URL, val authenticationEndpoint: URL, val jwksEndpoint: URL)
