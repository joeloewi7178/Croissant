@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
}


android {
    namespace = "com.joeloewi.croissant.domain"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.androidx.paging.common.ktx)
    implementation(libs.javax.inject)
}