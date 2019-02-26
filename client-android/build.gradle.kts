plugins {
    android("application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    dokka("android")
}

android {
    compileSdkVersion(SDK_TARGET)
    defaultConfig {
        minSdkVersion(SDK_MIN)
        targetSdkVersion(SDK_TARGET)
        multiDexEnabled = true
        applicationId = RELEASE_GROUP
        versionCode = 1
        versionName = RELEASE_VERSION
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDirs("src")
            res.srcDir("res")
            resources.srcDir("src")
        }
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    applicationVariants.all {
        generateBuildConfigProvider?.configure {
            enabled = false
        }
    }
    lintOptions {
        isAbortOnError = false
        isCheckTestSources = true
    }
    packagingOptions {
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude("META-INF/atomicfu.kotlin_module")
    }
}

ktlint()

dependencies {
    api(project(":client"))

    implementation(kotlinx("coroutines-android", VERSION_COROUTINES))
    implementation(slf4j("android"))
    implementation(hendraanggrian("defaults", "defaults-android", VERSION_DEFAULTS))

    implementation(androidx("multidex", version = VERSION_MULTIDEX))
    implementation(androidx("core", "core-ktx", "$VERSION_ANDROIDX-alpha03"))
    implementation(androidx("appcompat", version = "$VERSION_ANDROIDX-alpha01"))
    implementation(androidx("preference", version = "$VERSION_ANDROIDX-alpha01"))
    implementation(androidx("coordinatorlayout", version = "$VERSION_ANDROIDX-alpha01"))
    implementation(androidx("recyclerview", version = "$VERSION_ANDROIDX-alpha01"))
    implementation(material("$VERSION_ANDROIDX-alpha02"))

    implementation(hendraanggrian("material", "errorbar-ktx", "$VERSION_ANDROIDX-alpha02"))
    implementation(hendraanggrian("pikasso", "pikasso", VERSION_PIKASSO))
    implementation(
        hendraanggrian(
            "recyclerview", "recyclerview-paginated", VERSION_RECYCLERVIEW_PAGINATED
        )
    )
    implementation(hendraanggrian("bundler", "bundler-ktx", VERSION_BUNDLER))
    kapt(hendraanggrian("bundler", "bundler-compiler", VERSION_BUNDLER))

    implementation("com.jakewharton:process-phoenix:2.0.0")
    implementation("com.takisoft.preferencex:preferencex:1.0.0")
}

tasks {
    named<org.jetbrains.dokka.gradle.DokkaTask>("dokka") {
        outputDirectory = "$buildDir/docs"
        doFirst { file(outputDirectory).deleteRecursively() }
    }
}