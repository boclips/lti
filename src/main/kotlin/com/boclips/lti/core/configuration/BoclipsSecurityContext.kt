package com.boclips.lti.core.configuration

import com.boclips.security.EnableBoclipsSecurity
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
@EnableBoclipsSecurity
class BoclipsSecurityContext
