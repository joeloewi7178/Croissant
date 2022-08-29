@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    implementation(libs.androidx.paging.common)

    implementation(libs.javax.inject)
}