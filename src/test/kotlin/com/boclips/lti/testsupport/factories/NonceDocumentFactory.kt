package com.boclips.lti.testsupport.factories

import com.boclips.lti.v1p3.infrastructure.model.NonceDocument
import org.bson.types.ObjectId
import java.time.Instant
import java.util.UUID

object NonceDocumentFactory {
    fun sample(
        id: ObjectId = ObjectId(),
        value: String = UUID.randomUUID().toString(),
        createdAt: Instant = Instant.now()
    ) = NonceDocument(id = id, value = value, createdAt = createdAt)
}
