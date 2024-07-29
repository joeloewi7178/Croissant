plugins {
    alias(libs.plugins.croissant.android.feature)
    alias(libs.plugins.croissant.android.library.compose)
    alias(libs.plugins.croissant.android.hilt)
}

android {
    namespace = "com.joeloewi.croissant.feature.redeemcodes"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.domain)

    testImplementation(libs.hilt.android.testing)
}