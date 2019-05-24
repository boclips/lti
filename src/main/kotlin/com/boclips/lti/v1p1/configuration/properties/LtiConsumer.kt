package com.boclips.lti.v1p1.configuration.properties

import javax.validation.constraints.NotBlank

class LtiConsumer {
    @NotBlank
    lateinit var key: String
    @NotBlank
    lateinit var secret: String
}
