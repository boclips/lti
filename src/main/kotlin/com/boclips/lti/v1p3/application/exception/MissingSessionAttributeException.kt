package com.boclips.lti.v1p3.application.exception

import java.lang.RuntimeException

class MissingSessionAttributeException(attributeName: String) : RuntimeException("Session has no value for '$attributeName'")
