import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.*

group = "$RELEASE_GROUP.$RELEASE_ARTIFACT.core"
version = RELEASE_VERSION

plugins {
    java
    kotlin("jvm")
}

java {
    sourceSets {
        "main" {
            java.srcDir("src")
            resources.srcDir("res")
        }
    }
}

val ktlint by configurations.creating

dependencies {
    compile(kotlin("stdlib", VERSION_KOTLIN))
    compile(jodaTime())

    ktlint(ktlint())
}

tasks {
    "ktlint"(JavaExec::class) {
        get("check").dependsOn(this)
        group = VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    "ktlintFormat"(JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath = ktlint
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }
}