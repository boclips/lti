package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.converter.MessageConverter
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.validator.DeepLinkingRequestValidator
import com.boclips.lti.v1p3.domain.service.HandleDeepLinkingMessage
import java.net.URL
import javax.servlet.http.HttpSession

class HandleDeepLinkingRequest(private val handleDeepLinkingMessage: HandleDeepLinkingMessage) {
    operator fun invoke(idToken: DecodedJwtToken, session: HttpSession): URL {
        DeepLinkingRequestValidator.assertIsValid(idToken)

        return handleDeepLinkingMessage(
            message = MessageConverter.toDeepLinkingMessage(idToken),
            session = session
        )
    }
}
