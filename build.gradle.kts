buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.hendraanggrian:rsync:0.6")
        classpath("com.hendraanggrian:buildconfig:0.4")
    }
}

allprojects {
    repositories {
        jcenter()
        maven(url = "https://dl.bintray.com/hendraanggrian/maven")
        maven(url = "https://dl.bintray.com/kotlin/exposed/")
    }
}
