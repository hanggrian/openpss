package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.util.getResourceAsStream
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

enum class License(val owner: String, val repo: String, val homepage: String) {
    APACHE_COMMONS_LANG(
        "Apache",
        "Commons Lang",
        "https://commons.apache.org/lang"),
    APACHE_COMMONS_VALIDATOR(
        "Apache",
        "Commons Validator",
        "https://commons.apache.org/validator"),
    APACHE_POI(
        "Apache",
        "POI",
        "https://poi.apache.org"),
    GOOGLE_GUAVA(
        "Google",
        "Guava",
        "https://github.com/google/guava"),
    HENDRAANGGRIAN_JAVAFXX(
        "Hendra Anggrian",
        "javafxx",
        "https://github.com/hendraanggrian/javafxx"),
    JETBRAINS_KOTLIN(
        "JetBrains",
        "Kotlin",
        "http://kotlinlang.org"),
    JODAORG_JODA_TIME(
        "JodaOrg",
        "Joda-Time",
        "www.joda.org/joda-time"),
    MONGODB_MONGO_JAVA_DRIVER(
        "MongoDB",
        "Mongo Java Driver",
        "https://mongodb.github.io/mongo-java-driver/"),
    SLF4J_LOG4J12(
        "Slf4j",
        "Log4j12",
        "https://www.slf4j.org");

    suspend fun getContent(): String = withContext(DefaultDispatcher) {
        getResourceAsStream("/license/${name.toLowerCase()}.txt").use {
            BufferedReader(InputStreamReader(it)).lines().collect(Collectors.joining("\n"))
        }
    }
}