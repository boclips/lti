package com.boclips.lti.workshop.extensions

import com.boclips.lti.workshop.sealed.Animal
import com.boclips.lti.workshop.sealed.BoJackHorseman

fun Animal.run() = "Running fast, ${talk()}"

fun BoJackHorseman.run() = "No way, heading for the bar..."

val Animal.talkLength: Int
    get() = talk().length
