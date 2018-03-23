import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.r.RTask
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.*

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
    "test" {
        java.srcDir("tests/src")
    }
}

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.creating

dependencies {
    compile(kotlin("stdlib", kotlinVersion))
    compile(kotlinx("coroutines-javafx", coroutinesVersion))
    compile(hendraanggrian("ktfx-layouts", ktfxVersion, "ktfx"))
    compile(hendraanggrian("ktfx-listeners", ktfxVersion, "ktfx"))
    compile(hendraanggrian("ktfx-coroutines", ktfxVersion, "ktfx"))
    compile(jodaTime())
    compile(commonsValidator())

    testImplementation(kotlin("test", kotlinVersion))
    testImplementation(testFX("core"))
    testImplementation(testFX("junit"))

    ktlint(ktlint())
}

tasks {
    withType<RTask> {
        resourcesDir = "sceneres"
    }

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

    withType<ShadowJar> {
        destinationDir = buildDir.resolve("release")
        baseName = "$releaseArtifact-scene"
        version = releaseVersion
        classifier = null
    }
}
