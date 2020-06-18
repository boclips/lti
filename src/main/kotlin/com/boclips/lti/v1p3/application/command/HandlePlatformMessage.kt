package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.converter.MessageConverter
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.validator.ResourceLinkMessageValidator
import com.boclips.lti.v1p3.domain.exception.UnsupportedMessageTypeException
import java.net.URL
import javax.servlet.http.HttpSession

class HandlePlatformMessage(
    private val handleResourceLinkMessage: HandleResourceLinkMessage,
    private val handleDeepLinkingMessage: HandleDeepLinkingMessage
) {
    operator fun invoke(idToken: DecodedJwtToken, session: HttpSession, state: String): URL {
        return when (idToken.messageTypeClaim) {
            "LtiResourceLinkRequest" -> ResourceLinkMessageValidator
                .assertIsValid(token = idToken, state = state, session = session)
                .let {
                    handleResourceLinkMessage(
                        message = MessageConverter.toResourceLinkMessage(
                            idToken
                        ),
                        session = session
                    )
                }
            "LtiDeepLinkingRequest" -> handleDeepLinkingMessage(
                message = MessageConverter.toDeepLinkingMessage(idToken),
                session = session
            )
            else -> throw UnsupportedMessageTypeException(idToken.messageTypeClaim ?: "<null>")
        }
    }
}
