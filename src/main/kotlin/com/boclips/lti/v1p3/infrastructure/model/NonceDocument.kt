package com.boclips.lti.v1p3.infrastructure.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "nonces")
data class NonceDocument(
    @Id
    val id: ObjectId,
    val value: String,
    val createdAt: Instant
)
