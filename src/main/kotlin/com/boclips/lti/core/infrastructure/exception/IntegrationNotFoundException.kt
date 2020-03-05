package com.boclips.lti.core.infrastructure.exception

import java.lang.RuntimeException

class IntegrationNotFoundException(integrationId: String) : RuntimeException("Integration '$integrationId' was not found")
