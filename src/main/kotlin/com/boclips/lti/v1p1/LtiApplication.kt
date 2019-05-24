package com.boclips.lti.v1p1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LTIApplication

fun main(args: Array<String>) {
    runApplication<LTIApplication>(*args)
}
