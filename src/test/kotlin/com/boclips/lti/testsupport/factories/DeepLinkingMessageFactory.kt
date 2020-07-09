package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.domain.model.DeepLinkingMessage
import java.net.URL

object DeepLinkingMessageFactory {
    fun sample(
        issuer: URL = URL("http://issuer.com"),
        returnUrl: URL = URL("https://returnurl.com"),
        deploymentId: String = "deployment-id",
        data: String? = "data"
    ): DeepLinkingMessage {
        return DeepLinkingMessage(
            issuer = issuer,
            returnUrl = returnUrl,
            deploymentId = deploymentId,
            data = data
        )
    }
}
