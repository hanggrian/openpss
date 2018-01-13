package com.wijayaprinting.data

import com.wijayaprinting.core.Listable
import com.wijayaprinting.core.Resourced
import javafx.collections.ObservableList
import kotfx.observableListOf
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

data class License(val owner: String, val name: String, val homepage: String) {

    fun getContent(resourced: Resourced): String = resourced.getResourceAsStream("/${owner.shorten}_${name.shorten}.txt").use { return BufferedReader(InputStreamReader(it)).lines().collect(Collectors.joining("\n")) }

    private val String.shorten: String get() = toLowerCase().replace(' ', '_').replace('/', '_').replace('-', '_')

    companion object : Listable<License> {
        override fun listAll(): ObservableList<License> = observableListOf(
                License("Apache", "Commons Lang", "https://commons.apache.org/lang"),
                License("Apache", "Commons Math", "https://commons.apache.org/math"),
                License("Apache", "Commons Validator", "https://commons.apache.org/validator"),
                License("Apache", "POI OOXML", "https://poi.apache.org"),
                License("Google", "Guava", "https://github.com/google/guava"),
                License("Hendra Anggrian", "KotFX", "https://github.com/hendraanggrian/kotfx"),
                License("JetBrains", "Kotlin", "http://kotlinlang.org"),
                License("JodaOrg", "Joda-Time", "www.joda.org/joda-time"),
                License("MySQL", "MySQL Connector/J", "https://github.com/mysql/mysql-connector-j"),
                License("ReactiveX", "RxJavaFX", "https://github.com/ReactiveX/RxJavaFX"),
                License("ReactiveX", "RxKotlin", "https://github.com/ReactiveX/RxKotlin"),
                License("Slf4j", "Log4j12", "https://www.slf4j.org")
        )
    }
}