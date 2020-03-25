package com.boclips.lti.core.configuration

import com.boclips.lti.core.infrastructure.model.IntegrationDocument
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@Configuration("coreDatabaseContext")
class DatabaseContext(
    private val mongoTemplate: MongoTemplate
) {
    @EventListener(ApplicationReadyEvent::class)
    fun initIndicesAfterStartup() {
        mongoTemplate.indexOps(IntegrationDocument::class.java).ensureIndex(
            Index().on(
                IntegrationDocument::class.java.getDeclaredField("integrationId").name,
                Sort.Direction.ASC
            )
        )
    }
}
