package com.wijayaprinting.javafx

import java.util.*

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
object BuildConfig {

    val GROUP: String
    val ARTIFACT: String
    val VERSION: String
    val DESC: String
    val WEBSITE: String

    init {
        val config = Properties()
        val stream = BuildConfig.javaClass.getResourceAsStream(R.properties.javafx)
        config.load(stream)
        stream.close()
        GROUP = config.getProperty(R.javafx.group)
        ARTIFACT = config.getProperty(R.javafx.artifact)
        VERSION = config.getProperty(R.javafx.version)
        DESC = config.getProperty(R.javafx.desc)
        WEBSITE = config.getProperty(R.javafx.website)
    }
}