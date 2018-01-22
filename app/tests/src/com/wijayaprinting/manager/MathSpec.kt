package com.wijayaprinting.manager

import com.wijayaprinting.util.rounded
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(JUnitPlatform::class)
object MathSpec : Spek({

    given("a decimal") {
        val decimal = 2.345134
        it("should round with 2 scale") {
            assertEquals(decimal.rounded, 2.35)
        }
    }
})