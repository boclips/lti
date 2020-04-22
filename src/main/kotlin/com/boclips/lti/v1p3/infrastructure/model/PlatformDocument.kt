package com.boclips.lti.v1p3.infrastructure.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "platforms")
data class PlatformDocument(
    @Id
    val id: ObjectId,
    val issuer: String,
    val authenticationEndpoint: String,
    val jwksUrl: String,
    val clientId: String
)
