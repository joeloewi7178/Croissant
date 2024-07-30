plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.library.compose)
}

android {
    namespace = "com.joeloewi.croissant.core.ui"
}

dependencies {
    api(projects.core.model)
    api(projects.core.designsystem)

    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    testImplementation(libs.junit)
}