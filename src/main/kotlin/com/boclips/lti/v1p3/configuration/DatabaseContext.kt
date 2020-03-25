package com.boclips.lti.v1p3.configuration

import com.boclips.lti.v1p3.infrastructure.model.PlatformDocument
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@Configuration("onePointThreeDatabaseContext")
class DatabaseContext(
    private val mongoTemplate: MongoTemplate
) {
    @EventListener(ApplicationReadyEvent::class)
    fun initIndicesAfterStartup() {
        mongoTemplate.indexOps(PlatformDocument::class.java).ensureIndex(
            Index().on(
                PlatformDocument::class.java.getDeclaredField("issuer").name,
                Sort.Direction.ASC
            )
        )
    }
}
