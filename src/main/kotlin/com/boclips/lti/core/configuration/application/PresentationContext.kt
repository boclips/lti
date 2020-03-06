package com.boclips.lti.core.configuration.application

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.core.presentation.service.FormatDuration
import com.boclips.lti.core.presentation.service.SortByCollectionTitle
import com.boclips.lti.core.presentation.service.ToCollectionMetadata
import com.boclips.lti.core.presentation.service.ToVideoMetadata
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PresentationContext(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    @Bean
    fun formatDuration() = FormatDuration()

    @Bean
    fun sortByCollectionTitle() = SortByCollectionTitle()

    @Bean
    fun toCollectionMetadata() = ToCollectionMetadata(uriComponentsBuilderFactory)

    @Bean
    fun toVideoMetadata(formatDuration: FormatDuration): ToVideoMetadata {
        return ToVideoMetadata(uriComponentsBuilderFactory, formatDuration)
    }
}
