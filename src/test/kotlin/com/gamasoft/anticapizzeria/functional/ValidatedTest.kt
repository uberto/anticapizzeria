package com.gamasoft.anticapizzeria.functional

import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ValidatedTest {

    fun isOdd(x: Int): Validated<String, Int> = if (x % 2 == 1) Valid(x) else Invalid("even")


    @Test
    fun validResult() {

        assert(isOdd(3)).isEqualTo(Valid(3))
        assert(isOdd(4)).isEqualTo(Invalid("even"))

    }
}