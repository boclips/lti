package com.boclips.lti.core.configuration.application

import com.boclips.lti.core.application.service.UriComponentsBuilderFactory
import com.boclips.lti.core.presentation.service.FormatDuration
import com.boclips.lti.core.presentation.service.SortByCollectionTitle
import com.boclips.lti.core.presentation.service.ToCollectionViewModel
import com.boclips.lti.core.presentation.service.ToVideoViewModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PresentationContext(private val uriComponentsBuilderFactory: UriComponentsBuilderFactory) {
    @Bean
    fun formatDuration() = FormatDuration()

    @Bean
    fun sortByCollectionTitle() = SortByCollectionTitle()

    @Bean
    fun toCollectionMetadata() = ToCollectionViewModel(uriComponentsBuilderFactory)

    @Bean
    fun toVideoMetadata(formatDuration: FormatDuration): ToVideoViewModel {
        return ToVideoViewModel(uriComponentsBuilderFactory, formatDuration)
    }
}
