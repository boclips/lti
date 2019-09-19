package com.boclips.lti.workshop.extensions

fun String.hello() = "Oh hello, $this"

fun String.toString() = "Trying to override toString..."

fun String?.nullableHello() = "Oh hello, bummer you're null..."

fun String.Companion.theCompanionIsNotAlone() = "Added to a companion"
