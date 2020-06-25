package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.exception.UnsupportedMessageTypeException
import com.boclips.lti.v1p3.domain.model.MessageTypes
import java.net.URL
import javax.servlet.http.HttpSession

class HandlePlatformRequest(
    private val handleResourceLinkRequest: HandleResourceLinkRequest,
    private val handleDeepLinkingRequest: HandleDeepLinkingRequest
) {
    operator fun invoke(idToken: DecodedJwtToken, session: HttpSession, state: String): URL {
        return when (idToken.messageTypeClaim) {
            MessageTypes.ResourceLinkRequest -> handleResourceLinkRequest(idToken, session, state)
            MessageTypes.DeepLinkingRequest -> handleDeepLinkingRequest(idToken, session, state)
            else -> throw UnsupportedMessageTypeException(idToken.messageTypeClaim ?: "<null>")
        }
    }
}
