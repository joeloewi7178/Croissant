plugins {
    alias(libs.plugins.croissant.jvm.library)
    id("com.google.devtools.ksp")
}

dependencies {
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)

    testImplementation(libs.junit)
}