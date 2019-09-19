package com.boclips.lti.workshop.sealed

sealed class Animal { // Is abstract by default
    abstract val sound: String

    fun talk() = sound

}

data class Cat(override val sound: String) : Animal()

class Dog(private val dogSound: String) : Animal() {
    override val sound: String
        get() = dogSound

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }
}

object BoJackHorseman : Animal() {
    override val sound: String
        get() = "I'm a famous Hollywoo celebrity!"
}
