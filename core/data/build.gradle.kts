plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
}

android {
    namespace = "com.joeloewi.croissant.core.data"
    compileSdk = 34

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(libs.androidx.paging.common.ktx)
}