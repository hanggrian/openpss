buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.hendraanggrian:rsync:0.7")
        classpath("com.hendraanggrian:buildconfig:0.5")
    }
}

allprojects {
    repositories {
        jcenter()
        maven(url = "https://dl.bintray.com/hendraanggrian/maven")
        maven(url = "https://dl.bintray.com/kotlin/exposed/")
    }
}