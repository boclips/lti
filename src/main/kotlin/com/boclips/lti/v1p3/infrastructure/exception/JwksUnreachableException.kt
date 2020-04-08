package com.boclips.lti.v1p3.infrastructure.exception

class JwksUnreachableException(cause: Throwable) : RuntimeException("Unable to retrieve JWKS", cause)
