plugins {
    `java-library`
    kotlin
    `kotlin-kapt`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
dependencies {

    implementation("androidx.paging:paging-common-ktx:3.1.1")

    //moshi
    implementation("com.squareup.moshi:moshi:${Versions.moshi}")
    implementation("com.squareup.moshi:moshi-adapters:${Versions.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}")

    implementation("javax.inject:javax.inject:1")
}
