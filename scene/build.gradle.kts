plugins {
    `java-library`
    kotlin("jvm")
    idea
    generation("r")
    shadow
}

group = "$RELEASE_GROUP.scene"
version = RELEASE_VERSION

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

kotlin.experimental.coroutines = org.jetbrains.kotlin.gradle.dsl.Coroutines.ENABLE

val ktlint by configurations.registering

dependencies {
    compile(kotlin("stdlib", VERSION_KOTLIN))
    compile(kotlinx("coroutines-javafx", VERSION_COROUTINES))
    compile(controlsFx())
    compile(hendraanggrian("ktfx", "ktfx-layouts", VERSION_KTFX))
    compile(hendraanggrian("ktfx", "ktfx-listeners", VERSION_KTFX))
    compile(hendraanggrian("ktfx", "ktfx-coroutines", VERSION_KTFX))
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
    "generateR"(com.hendraanggrian.generation.r.RTask::class) {
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

    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        destinationDir = buildDir.resolve("release")
        baseName = "$RELEASE_ARTIFACT-scene"
        version = RELEASE_VERSION
        classifier = null
    }
}