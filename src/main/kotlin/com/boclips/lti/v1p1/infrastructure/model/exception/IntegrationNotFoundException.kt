package com.boclips.lti.v1p1.infrastructure.model.exception

import java.lang.RuntimeException

class IntegrationNotFoundException(integrationId: String) : RuntimeException("Integration '$integrationId' was not found")
