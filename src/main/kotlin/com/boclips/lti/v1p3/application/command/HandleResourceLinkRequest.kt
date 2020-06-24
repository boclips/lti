package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.converter.MessageConverter
import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.application.validator.ResourceLinkRequestValidator
import com.boclips.lti.v1p3.domain.service.HandleResourceLinkMessage
import java.net.URL
import javax.servlet.http.HttpSession

class HandleResourceLinkRequest(private val handleResourceLinkMessage: HandleResourceLinkMessage) {
    operator fun invoke(idToken: DecodedJwtToken, session: HttpSession, state: String): URL {
        ResourceLinkRequestValidator.assertIsValid(token = idToken, state = state, session = session)

        return handleResourceLinkMessage(
            message = MessageConverter.toResourceLinkMessage(
                idToken
            ),
            session = session
        )
    }
}
