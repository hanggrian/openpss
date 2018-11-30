package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.util.getResourceAsStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.stream.Collectors

enum class License(val owner: String, val repo: String, val homepage: String) {
    APACHE_COMMONS_LANG(
        "Apache",
        "Commons Lang",
        "https://commons.apache.org/lang"
    ),
    APACHE_COMMONS_VALIDATOR(
        "Apache",
        "Commons Validator",
        "https://commons.apache.org/validator"
    ),
    APACHE_POI(
        "Apache",
        "POI",
        "https://poi.apache.org"
    ),
    GOOGLE_GUAVA(
        "Google",
        "Guava",
        "https://github.com/google/guava"
    ),
    HENDRAANGGRIAN_KTFX(
        "Hendra Anggrian",
        "Ktfx",
        "https://github.com/hendraanggrian/ktfx"
    ),
    JETBRAINS_KOTLIN(
        "JetBrains",
        "Kotlin",
        "http://kotlinlang.org"
    ),
    JODAORG_JODA_TIME(
        "JodaOrg",
        "Joda-Time",
        "www.joda.org/joda-time"
    ),
    MONGODB_MONGO_JAVA_DRIVER(
        "MongoDB",
        "Mongo Java Driver",
        "https://mongodb.github.io/mongo-java-driver/"
    ),
    SLF4J_LOG4J12(
        "Slf4j",
        "Log4j12",
        "https://www.slf4j.org"
    );

    suspend fun getContent(): String = withContext(Dispatchers.Default) {
        getResourceAsStream("/license/${name.toLowerCase()}.txt").use {
            BufferedReader(InputStreamReader(it)).lines().collect(Collectors.joining("\n"))
        }
    }
}