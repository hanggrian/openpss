package com.hendraanggrian.openpss

import org.junit.Assume
import org.junit.Before

interface SkipTravisTest {

    @Before
    @Throws(Exception::class)
    fun before() = Assume.assumeTrue(System.getProperty("user.name") != "travis")
}