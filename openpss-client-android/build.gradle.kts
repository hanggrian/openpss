plugins {
    android("application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

android {
    compileSdk = SDK_TARGET
    defaultConfig {
        minSdk = SDK_MIN
        targetSdk = SDK_TARGET
        multiDexEnabled = true
        applicationId = RELEASE_GROUP
        versionCode = 1
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        named("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.srcDir("src")
            res.srcDir("res")
            resources.srcDir("src")
        }
    }
    buildTypes {
        named("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    applicationVariants.all {
        generateBuildConfigProvider?.configure {
            enabled = false
        }
    }
    lint {
        isAbortOnError = false // temporarily
    }
    packagingOptions {
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude("META-INF/atomicfu.kotlin_module")
    }
}

ktlint()

dependencies {
    implementation(project(":$RELEASE_ARTIFACT-client"))

    implementation(kotlinx("coroutines-android", VERSION_COROUTINES))
    implementation(slf4j("android"))

    implementation(androidx("multidex", version = VERSION_MULTIDEX))
    implementation(androidx("core", "core-ktx"))
    implementation(androidx("appcompat"))
    implementation(androidx("preference"))
    implementation(androidx("swiperefreshlayout", version = "$VERSION_ANDROIDX-beta01"))
    implementation(androidx("coordinatorlayout"))
    implementation(androidx("recyclerview"))
    implementation(material())

    implementation(hendraanggrian("material", "errorbar-ktx", "$VERSION_ANDROIDX-beta01"))
    implementation(hendraanggrian("pikasso", "pikasso", VERSION_PIKASSO))
    implementation(hendraanggrian("recyclerview", "recyclerview-paginated", VERSION_RECYCLERVIEW_PAGINATED))
    implementation(hendraanggrian("prefy", "prefy-android", VERSION_PREFY))
    kapt(hendraanggrian("prefy", "prefy-compiler", VERSION_PREFY))
    implementation(hendraanggrian("bundler", "bundler-ktx", VERSION_BUNDLER))
    kapt(hendraanggrian("bundler", "bundler-compiler", VERSION_BUNDLER))

    implementation(jakewharton("process-phoenix", VERSION_PROCESSPHOENIX))
    implementation(preferencex("$VERSION_ANDROIDX-alpha05"))
}
