package com.boclips.lti.v1p3.configuration.properties

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

class RsaKeyPairProperties {
    @Min(0)
    var generationTimestamp: Long = -1
    @NotBlank
    var privateKey: String = ""
    @NotBlank
    var publicKey: String = ""
}
