package com.boclips.lti.v1p3.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@Component
@Validated
@ConfigurationProperties(prefix = "boclips.lti.v1p3")
class SigningKeysProperties {
    @Valid
    @NotEmpty
    var signingKeys: List<RsaKeyPairProperties> = emptyList()
}
