package com.boclips.lti.core.infrastructure.service

import com.boclips.users.api.httpclient.UsersClient

interface UsersClientFactory {
    fun getClient(integrationId: String): UsersClient
}
