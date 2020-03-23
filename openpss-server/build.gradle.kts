plugins {
    kotlin("jvm")
    idea
    hendraanggrian("r")
    hendraanggrian("buildconfig")
    hendraanggrian("locale")
    shadow
    application
}

group = RELEASE_GROUP
version = RELEASE_VERSION

application.mainClassName = "$RELEASE_GROUP.Server"

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("res")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

ktlint()

dependencies {
    implementation(project(":$RELEASE_ARTIFACT"))

    implementation(ktor("server-netty"))
    implementation(ktor("websockets"))
    implementation(ktor("gson"))

    implementation(logback("classic"))
    implementation(mongo("java-driver"))

    testImplementation(kotlin("test-junit", VERSION_KOTLIN))
    testImplementation(kotlin("reflect", VERSION_KOTLIN))
    testImplementation(slf4j("log4j12"))
}

tasks {
    named<com.hendraanggrian.r.RTask>("generateR") {
        resourcesDirectory = "res"
        properties {
            isWriteResourceBundle = true
        }
    }

    named<Jar>("jar") { manifest { attributes(mapOf("Main-Class" to application.mainClassName)) } }

    named<com.hendraanggrian.buildconfig.BuildConfigTask>("generateBuildConfig") {
        appName = "$RELEASE_NAME Server"
        debug = RELEASE_DEBUG
        website = RELEASE_WEBSITE
        addField("DATABASE_NAME", RELEASE_ARTIFACT)
        addField("DATABASE_USER", DATABASE_USER)
        addField("DATABASE_PASS", DATABASE_PASS)
    }

    named<com.hendraanggrian.locale.LocalizeJavaTask>("localizeJava") {
        outputDirectory = "res"
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        destinationDirectory.set(buildDir.resolve("releases"))
        archiveBaseName.set(RELEASE_ARTIFACT)
        archiveVersion.set(RELEASE_VERSION)
        archiveClassifier.set(null as String?)
    }
}

locale {
    resourceName = "string"

    "active_on" {
        en = "Active on %s:%s"
        id = "Aktif di %s:%s"
    }
    "about" {
        en = "About %s"
        id = "Tentang %s"
    }
    "quit" {
        en = "Quit"
        id = "Keluar"
    }

    "system_tray_is_unsupported" {
        en = "System tray is unsupported."
        id = "System tray tidak support."
    }
    "desktop_is_unsupported" {
        en = "Desktop is unsupported."
        id = "Desktop tidak support."
    }

    "contact_deleted" {
        en = "Deleted contact %s from %s"
        id = "Menghapus kontak %s dari %s"
    }
    "customer_edit" {
        en = "Edited customer %s"
        id = "Mengubah pelanggan %s"
    }
    "employee_delete" {
        en = "Deleted employee %s"
        id = "Menghapus pegawai %s"
    }
    "employee_edit" {
        en = "Edited employee %s"
        id = "Mengubah pelanggan %s"
    }
    "invoice_delete" {
        en = "Deleted invoice no %s from customer %s"
        id = "Menghapus faktur no %s dari pelanggan %s"
    }
    "payment_delete" {
        en = "Deleted payment %s from invoice no %s"
        id = "Menghapus pembayaran %s dari faktur %s"
    }
}
