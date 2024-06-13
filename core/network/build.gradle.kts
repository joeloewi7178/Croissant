plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
}

android {
    namespace = "com.joeloewi.croissant.core.network"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.model)

    ksp(libs.moshi.kotlin.codegen.get())

    implementation(platform(libs.retrofit.bom))
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.scalars)
    ksp(libs.retrofit.response.type.keeper)

    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3.logging.interceptor)

    implementation(libs.sandwich)
    implementation(libs.sandwich.retrofit)

    //html parsing
    implementation(libs.jsoup)
}