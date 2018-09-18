import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.hendraanggrian.generation.r.RTask
import org.jetbrains.kotlin.gradle.dsl.Coroutines

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

kotlin.experimental.coroutines = Coroutines.ENABLE

val ktlint by configurations.registering

dependencies {
    compile(kotlin("stdlib", VERSION_KOTLIN))
    compile(kotlinx("coroutines-javafx", VERSION_COROUTINES))
    compile(controlsFx())
    compile(hendraanggrian("javafxx", "layouts-ktx", VERSION_JAVAFXX))
    compile(hendraanggrian("javafxx", "listeners-ktx", VERSION_JAVAFXX))
    compile(hendraanggrian("javafxx", "coroutines-ktx", VERSION_JAVAFXX))
    compile(jodaTime())
    compile(commonsValidator())

    testImplementation(kotlin("test", VERSION_KOTLIN))
    testImplementation(testFx("core"))
    testImplementation(testFx("junit"))

    ktlint {
        invoke(ktlint())
    }
}

tasks {
    "generateR"(RTask::class) {
        resourcesDir = projectDir.resolve("sceneres")
    }

    val ktlint by registering(JavaExec::class) {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        inputs.dir("src")
        outputs.dir("src")
        description = "Check Kotlin code style."
        classpath(configurations["ktlint"])
        main = "com.github.shyiko.ktlint.Main"
        args("src/**/*.kt")
    }
    "check" {
        dependsOn(ktlint)
    }
    register("ktlintFormat", JavaExec::class) {
        group = "formatting"
        inputs.dir("src")
        outputs.dir("src")
        description = "Fix Kotlin code style deviations."
        classpath(configurations["ktlint"])
        main = "com.github.shyiko.ktlint.Main"
        args("-F", "src/**/*.kt")
    }

    withType<ShadowJar> {
        destinationDir = buildDir.resolve("release")
        baseName = "$RELEASE_ARTIFACT-scene"
        version = RELEASE_VERSION
        classifier = null
    }
}