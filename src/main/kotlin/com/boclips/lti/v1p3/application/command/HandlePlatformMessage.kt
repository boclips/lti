package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.converter.MessageConverter
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.validator.ResourceLinkMessageValidator
import com.boclips.lti.v1p3.domain.exception.UnsupportedMessageTypeException
import java.net.URL
import javax.servlet.http.HttpSession

class HandlePlatformMessage(
    private val handleResourceLinkMessage: HandleResourceLinkMessage
) {
    operator fun invoke(idToken: DecodedJwtToken, session: HttpSession): URL {
        when (idToken.messageTypeClaim) {
            "LtiResourceLinkRequest" -> return ResourceLinkMessageValidator
                .assertContainsAllRequiredClaims(idToken)
                .let {
                    handleResourceLinkMessage(
                        MessageConverter.toResourceLinkMessage(
                            idToken
                        ),
                        session
                    )
                }
            else -> throw UnsupportedMessageTypeException(idToken.messageTypeClaim ?: "<null>")
        }
    }
}
