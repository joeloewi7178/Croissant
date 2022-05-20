plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
}

java {
    disableAutoTargetJvm()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {

    implementation("androidx.paging:paging-common-ktx:${Versions.paging}")

    //moshi
    implementation("com.squareup.moshi:moshi:${Versions.moshi}")
    implementation("com.squareup.moshi:moshi-adapters:${Versions.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}")

}
