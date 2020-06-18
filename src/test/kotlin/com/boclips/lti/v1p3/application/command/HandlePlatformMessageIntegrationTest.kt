package com.boclips.lti.v1p3.application.command

import com.boclips.lti.testsupport.AbstractSpringIntegrationTest
import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.domain.exception.UnsupportedMessageTypeException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession

class HandlePlatformMessageIntegrationTest : AbstractSpringIntegrationTest() {
    @Autowired
    private lateinit var handlePlatformMessage: HandlePlatformMessage

    @Test
    fun `throws an exception when the message is not of a supported type`() {
        assertThrows<UnsupportedMessageTypeException> {
            handlePlatformMessage(
                idToken = DecodedJwtTokenFactory.sample(
                    messageTypeClaim = "this won't work"
                ),
                session = MockHttpSession(),
                state = "state"
            )
        }
    }

    @Test
    fun `throws an exception when the message type is null`() {
        assertThrows<UnsupportedMessageTypeException> {
            handlePlatformMessage(
                idToken = DecodedJwtTokenFactory.sample(
                    messageTypeClaim = null
                ),
                session = MockHttpSession(),
                state = "state"
            )
        }
    }
}
