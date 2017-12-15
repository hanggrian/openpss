buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath(hendraanggrian("rsync", "0.7"))
        classpath(hendraanggrian("buildconfig", "0.5"))
    }
}

allprojects {
    repositories {
        jcenter()
        maven(url = "https://dl.bintray.com/hendraanggrian/maven")
        maven(url = "https://dl.bintray.com/kotlin/exposed/")
    }
}