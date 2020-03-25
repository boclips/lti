package com.boclips.lti.v1p3.domain.repository

import com.boclips.lti.v1p3.domain.model.Platform
import java.net.URL

interface PlatformRepository {
    fun getByIssuer(issuer: URL): Platform
}
