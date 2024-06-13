plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
}

android {
    namespace = "com.joeloewi.croissant.core.datastore"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}

dependencies {
    api(libs.androidx.dataStore.core)
    api(projects.core.datastoreProto)
    api(projects.core.model)
}