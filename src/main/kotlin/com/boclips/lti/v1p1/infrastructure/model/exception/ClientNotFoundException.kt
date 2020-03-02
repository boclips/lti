package com.boclips.lti.v1p1.infrastructure.model.exception

class ClientNotFoundException(consumerKey: String) : RuntimeException("Client not found for consumer key '$consumerKey'")
