package com.boclips.lti.v1p3.application.service

interface NonceService {
    fun hasNonceBeenUsedAlready(value: String): Boolean
    fun storeNonce(value: String)
}
