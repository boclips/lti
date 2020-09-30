package com.boclips.lti.core.infrastructure.service

import com.boclips.users.api.httpclient.IntegrationsClient

interface IntegrationsClientFactory {
    fun getClient(integrationId: String): IntegrationsClient
}
