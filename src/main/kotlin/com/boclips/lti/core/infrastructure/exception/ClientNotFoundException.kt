package com.boclips.lti.core.infrastructure.exception

class ClientNotFoundException(consumerKey: String) : RuntimeException("Client not found for consumer key '$consumerKey'")
