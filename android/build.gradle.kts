plugins {
    android("application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    dokka("android")
}

android {
    compileSdkVersion(SDK_TARGET)
    buildToolsVersion(BUILD_TOOLS)
    defaultConfig {
        minSdkVersion(SDK_MIN)
        targetSdkVersion(SDK_TARGET)
        applicationId = RELEASE_GROUP
        versionCode = 1
        versionName = RELEASE_VERSION
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
    lintOptions {
        isAbortOnError = false
    }
}

ktlint()

dependencies {
    api(project(":core"))

    implementation(kotlinx("coroutines-android", VERSION_COROUTINES))
    implementation(slf4j("android"))

    implementation(androidx("core", "core-ktx"))
    implementation(androidx("appcompat"))
    implementation(androidx("preference"))
    implementation(androidx("coordinatorlayout"))
    implementation(androidx("recyclerview"))
    implementation(material())

    implementation(hendraanggrian("pikasso", version = VERSION_PIKASSO))
    implementation(hendraanggrian("recyclerview", "recyclerview-paginated", VERSION_RECYCLERVIEW_PAGINATED))
    implementation(hendraanggrian("bundler", "bundler-ktx", VERSION_BUNDLER))
    kapt(hendraanggrian("bundler", "bundler-compiler", VERSION_BUNDLER))

    implementation("com.takisoft.preferencex:preferencex:$VERSION_ANDROIDX")
}