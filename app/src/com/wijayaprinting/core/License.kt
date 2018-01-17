package com.wijayaprinting.core

import javafx.collections.ObservableList
import kotfx.observableListOf
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors.joining

enum class License(val owner: String, val repo: String, val homepage: String) {
    APACHE_COMMONS_LANG("Apache", "commons-lang", "https://commons.apache.org/lang"),
    APACHE_COMMONS_MATH("Apache", "commons-math", "https://commons.apache.org/math"),
    APACHE_COMMONS_VALIDATOR("Apache", "commons-validator", "https://commons.apache.org/validator"),
    APACHE_POI_OOXML("Apache", "POI OOXML", "https://poi.apache.org"),
    GOOGLE_GUAVA("Google", "Guava", "https://github.com/google/guava"),
    HENDRAANGGRIAN_KOTFX("Hendra Anggrian", "kotfx", "https://github.com/hendraanggrian/kotfx"),
    JETBRAINS_KOTLIN("JetBrains", "Kotlin", "http://kotlinlang.org"),
    JODAORG_JODA_TIME("JodaOrg", "Joda-Time", "www.joda.org/joda-time"),
    REACTIVEX_RXJAVAFX("ReactiveX", "RxJavaFX", "https://github.com/ReactiveX/RxJavaFX"),
    REACTIVEX_RXKOTLIN("ReactiveX", "RxKotlin", "https://github.com/ReactiveX/RxKotlin");

    fun getContent(resourced: Resourced): String = resourced.getResourceAsStream("/${name.toLowerCase()}.txt").use { return BufferedReader(InputStreamReader(it)).lines().collect(joining("\n")) }

    companion object : Listable<License> {
        override fun listAll(): ObservableList<License> = observableListOf(*values())
    }
}