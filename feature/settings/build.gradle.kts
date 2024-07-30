plugins {
    alias(libs.plugins.croissant.android.feature)
    alias(libs.plugins.croissant.android.library.compose)
    alias(libs.plugins.croissant.android.hilt)
}

android {
    namespace = "com.joeloewi.croissant.feature.settings"
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(projects.core.data)
    implementation(projects.domain)

    implementation(libs.gms.play.services.oss.licenses)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.hilt.android.testing)
}