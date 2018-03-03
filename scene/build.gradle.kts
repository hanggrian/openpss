import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.r.RTask
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*

group = "$releaseGroup.$releaseArtifact.scene"
version = releaseVersion

plugins {
    `java-library`
    kotlin("jvm")
    idea
    r
    shadow
}

java.sourceSets {
    "main" {
        java.srcDir("src")
        resources.srcDir("sceneres")
    }
}

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.creating

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(kotlinx("coroutines-javafx", coroutinesVersion))
    compile(hendraanggrian("kotfx-layouts", kotfxVersion))
    compile(hendraanggrian("kotfx-listeners", kotfxVersion))
    compile(hendraanggrian("kotfx-coroutines", kotfxVersion))
    compile(jodaTime())
    compile(commonsValidator())
    ktlint(ktlint())
}

tasks {
    withType<RTask> {
        resourcesDir = "sceneres"
    }

    val ktlint by creating(JavaExec::class) {
        group = "verification"
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath = configurations["ktlint"]
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    get("check").dependsOn(ktlint)
    "ktlintFormat"(JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath = configurations["ktlint"]
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    withType<ShadowJar> {
        destinationDir = buildDir.resolve("release")
        baseName = "$releaseArtifact-scene"
        version = releaseVersion
        classifier = null
    }
}
