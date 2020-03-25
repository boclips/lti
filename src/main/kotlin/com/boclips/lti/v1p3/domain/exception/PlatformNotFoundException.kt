package com.boclips.lti.v1p3.domain.exception

import java.net.URL

class PlatformNotFoundException(issuer: URL) : RuntimeException("Platform not found for issuer $issuer")
