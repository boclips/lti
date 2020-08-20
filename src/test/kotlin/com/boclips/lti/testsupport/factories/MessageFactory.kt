package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.domain.model.ResourceLinkMessage
import java.net.URL

object MessageFactory {
    fun sampleResourceLinkMessage(
        issuer: URL = URL("https://lms.com/learn-it"),
        requestedResource: URL = URL("https://tool.com/resource/123"),
        subject: String = "user-1234"
    ) = ResourceLinkMessage(issuer = issuer, requestedResource = requestedResource, subject = subject)
}
