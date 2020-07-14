package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.application.converter.RsaKeyPairConverter
import java.time.Instant

object RsaKeyPairFactory {
    fun sample(generationTimestamp: Long = Instant.now().epochSecond) =
        RsaKeyPairConverter.toSigningKeyPair(RsaKeyPairPropertiesFactory.sample(generationTimestamp = generationTimestamp))
}
