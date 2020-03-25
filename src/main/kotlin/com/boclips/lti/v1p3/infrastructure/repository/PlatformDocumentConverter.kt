package com.boclips.lti.v1p3.infrastructure.repository

import com.boclips.lti.v1p3.domain.model.Platform
import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import java.net.URL

object PlatformDocumentConverter {
    fun toDomainInstance(document: PlatformDocument) =
        Platform(issuer = URL(document.issuer), authenticationEndpoint = URL(document.authenticationEndpoint))
}
