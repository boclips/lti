package com.boclips.lti.workshop.extensions

import com.boclips.lti.workshop.sealed.Animal
import com.boclips.lti.workshop.sealed.Cat
import com.boclips.lti.workshop.sealed.Dog
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExtensionsTest {
    @Test
    fun `extensions can be used to add features to existing types`() {
        println("Boclips".hello())
    }

    @Test
    fun `extensions can have a null receiver`() {
        val thisIsNull: String? = null

        println(thisIsNull.nullableHello())
    }

    private fun letItRun(animal: Animal) {
        println(animal.run())
    }

    @Test
    fun `they are resolved statically at compile time`() {
        letItRun(Cat("meow!!!"))
    }

    @Test
    fun `read properties can be added too`() {
        assertThat(Dog("woof").talkLength).isEqualTo(4)
    }

    @Test
    fun `can be added to companion objects`() {
        println(String.theCompanionIsNotAlone())
    }

    @Test
    fun `extensions can be class members`() {
        println(DogPerson().spendTimeWith(Dog("Woof!")))
    }

    @Test
    fun `member extensions have multiple (2) call receivers`() {
        println(DogPerson().whoIsIt(Dog("Woof!")))
    }
}
