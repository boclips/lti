package com.boclips.lti.workshop.sealed

import org.junit.jupiter.api.Test

class AnimalTest {
    private fun doAnimalStuff(animal: Animal): String {
        return animal.talk()
    }

    @Test
    fun `let the animal do the talking`() {
        println(doAnimalStuff(Cat("Meow")))
    }
}
