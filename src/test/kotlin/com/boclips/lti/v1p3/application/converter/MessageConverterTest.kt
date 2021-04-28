package com.boclips.lti.v1p3.application.converter

import com.boclips.lti.testsupport.factories.DecodedJwtTokenFactory
import com.boclips.lti.v1p3.application.model.DeepLinkingSettingsClaim
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.net.URL

class MessageConverterTest {

    @Test
    fun `should convert token to message`() {
        val token = DecodedJwtTokenFactory.sample(
            issuerClaim = "http://an-issuer",
            targetLinkUriClaim = "example-target",
            deepLinkingSettingsClaim = DeepLinkingSettingsClaim(
                deepLinkReturnUrl = "http://a-deep-link-return-url",
                acceptPresentationDocumentTargets = null,
                acceptTypes = null,
                data = "some-data"
            ),
            deploymentIdClaim = "a-deployment",
            subjectClaim = "a-subject"
        )

        val message = MessageConverter.toDeepLinkingMessage(token)
        assertThat(message.targetUri).isEqualTo("example-target")
        assertThat(message.returnUrl).isEqualTo(URL("http://a-deep-link-return-url"))
        assertThat(message.issuer).isEqualTo(URL("http://an-issuer"))
        assertThat(message.data).isEqualTo("some-data")
        assertThat(message.deploymentId).isEqualTo("a-deployment")
        assertThat(message.subject).isEqualTo("a-subject")
    }
}
