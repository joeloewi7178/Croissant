plugins {
    alias(libs.plugins.croissant.android.library)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.joeloewi.croissant.core.model"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)
}