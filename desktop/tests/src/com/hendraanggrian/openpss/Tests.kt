package com.hendraanggrian.openpss

import org.junit.Assume

fun assumeNotInTravis() = Assume.assumeTrue(System.getProperty("user.name") != "travis")