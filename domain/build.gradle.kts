@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    kotlin("kapt")
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.androidx.paging.common)

    //moshi
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.javax.inject)
}