package com.boclips.lti.workshop.extensions

import com.boclips.lti.workshop.sealed.Dog

class DogPerson() {
    private fun Dog.play() = "${activityType()}. ${talk()}"

    private fun activityType() = "Fooling around"

    fun spendTimeWith(dog: Dog) = dog.play()

    private fun Dog.whoAreYou() = toString()

    fun whoIsIt(dog: Dog) = dog.whoAreYou()
}
