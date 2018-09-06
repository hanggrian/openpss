import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.generation.r.RTask
import org.codehaus.groovy.ast.tools.GeneralUtils.args
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.Coroutines.*
import org.gradle.language.base.plugins.LifecycleBasePlugin.*
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

group = "$RELEASE_GROUP.scene"
version = RELEASE_VERSION

plugins {
    `java-library`
    kotlin("jvm")
    idea
    generation("r")
    shadow
}

sourceSets {
    getByName("main") {
        java.srcDir("src")
        resources.srcDir("sceneres")
    }
    getByName("test") {
        java.srcDir("tests/src")
        resources.srcDir("tests/res")
    }
}

kotlin.experimental.coroutines = ENABLE

val ktlint by configurations.registering

dependencies {
    compile(kotlin("stdlib", VERSION_KOTLIN))
    compile(kotlinx("coroutines-javafx", VERSION_COROUTINES))
    compile(controlsFX())
    compile(hendraanggrian("javafxx", "layouts-ktx", VERSION_JAVAFXX))
    compile(hendraanggrian("javafxx", "listeners-ktx", VERSION_JAVAFXX))
    compile(hendraanggrian("javafxx", "coroutines-ktx", VERSION_JAVAFXX))
    compile(jodaTime())
    compile(commonsValidator())

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(testFX("core"))
    testImplementation(testFX("junit"))

    ktlint(this, ktlint())
}

tasks {
    withType<RTask> {
        resourcesDir = projectDir.resolve("sceneres")
    }

    register("ktlint", JavaExec::class) {
        get("check").dependsOn(ktlint)
        group = VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(ktlint())
        main = "com.github.shyiko.ktlint.Main"
        args("src/**.kt")
    }
    register("ktlintformat", JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(ktlint())
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src*.kt")
    }

    withType<ShadowJar> {
        destinationDir = buildDir.resolve("release")
        baseName = "$RELEASE_ARTIFACT-scene"
        version = RELEASE_VERSION
        classifier = null
    }
}