package com.boclips.lti.v1p3.application.command

import com.boclips.lti.v1p3.application.model.DecodedJwtToken
import com.boclips.lti.v1p3.domain.model.getBoclipsUserId
import com.boclips.lti.v1p3.domain.model.setBoclipsUserId
import com.boclips.lti.v1p3.domain.model.setIntegrationId
import com.boclips.lti.v1p3.domain.repository.PlatformRepository
import com.boclips.lti.v1p3.domain.service.SynchroniseUser
import java.net.URL
import javax.servlet.http.HttpSession

class SetUpSession(
    private val synchroniseUser: SynchroniseUser,
    private val platformRepository: PlatformRepository
) {
    operator fun invoke(session: HttpSession, token: DecodedJwtToken) {
        val platform = platformRepository.getByIssuer(URL(token.issuerClaim))
        val integrationId = platform.issuer.toString()
        session.setIntegrationId(integrationId)


        if(session.getBoclipsUserId().isNullOrBlank()) {
            var internalUserId: String? = null
            try {
            internalUserId = synchroniseUser(
                integrationId = token.issuerClaim!!,
                externalUserId = token.subjectClaim!!,
                deploymentId = token.deploymentIdClaim!!
            )} catch (e: Exception) {}
            session.setBoclipsUserId(internalUserId ?: token.subjectClaim!!)
        }
    }
}
