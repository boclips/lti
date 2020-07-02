package com.boclips.lti.v1p3.application.exception

class IntegrationNotFoundException(val integrationId: String) :
    RuntimeException("Cannot find integration with id: $integrationId")
