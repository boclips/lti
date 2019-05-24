package com.boclips.lti.v1p1.domain.service

import org.imsglobal.lti.launch.LtiVerificationResult
import org.springframework.stereotype.Service

@Service
class IsLaunchRequestValid {
    operator fun invoke(result: LtiVerificationResult): Boolean {
        return result.success && result.ltiLaunchResult.resourceLinkId?.isNotBlank() ?: false
    }
}
