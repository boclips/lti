package com.boclips.lti.v1p3.application.validator

import com.boclips.lti.v1p3.application.exception.LtiMessageClaimValidationException
import com.boclips.lti.v1p3.application.model.DecodedJwtToken

object DeepLinkingRequestValidator {
    fun assertIsValid(token: DecodedJwtToken) {
        if (token.deepLinkingSettingsClaim == null) throw LtiMessageClaimValidationException("deep_linking_settings have not been provided")
        if (token.deepLinkingSettingsClaim.deepLinkReturnUrl == null) throw LtiMessageClaimValidationException("deep_link_return_url has not been provided")
        if (!token.deepLinkingSettingsClaim.deepLinkReturnUrl.isURLString()) throw LtiMessageClaimValidationException("deep_link_return_url is not a valid URL")
        if (token.deepLinkingSettingsClaim.acceptTypes?.contains("ltiResourceLink") != true) throw LtiMessageClaimValidationException(
            "accept_types does not contain 'ltiResourceLink'"
        )
        if (token.deepLinkingSettingsClaim.acceptPresentationDocumentTargets == null) throw LtiMessageClaimValidationException(
            "accept_presentation_document_targets is a required LtiDeepLinkingRequest message parameter"
        )
    }
}
