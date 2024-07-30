plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.joeloewi.croissant.core.data"
    compileSdk = 34

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)
    api(projects.core.system)

    implementation(libs.androidx.paging.common.ktx)
    compileOnly(libs.compose.stable.marker)
    implementation(libs.kotlinx.serialization.json)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    testImplementation(libs.junit)
}