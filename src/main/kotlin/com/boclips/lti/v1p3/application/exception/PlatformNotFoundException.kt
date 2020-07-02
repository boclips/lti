package com.boclips.lti.v1p3.application.exception

class PlatformNotFoundException(clientId: String) :
    RuntimeException("Could not find a platform with from clientId: $clientId")
