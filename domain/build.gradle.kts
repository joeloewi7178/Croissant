@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.androidx.paging.common.ktx)

    implementation(libs.javax.inject)
}