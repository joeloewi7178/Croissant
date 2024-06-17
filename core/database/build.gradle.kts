plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
    alias(libs.plugins.croissant.android.room)
}

android {
    namespace = "com.joeloewi.croissant.core.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    testImplementation(libs.junit)
}