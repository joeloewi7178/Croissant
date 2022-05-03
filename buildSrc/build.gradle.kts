plugins {
    `kotlin-dsl`
}

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    implementation("com.google.gms:google-services:4.3.10")
    implementation("com.android.tools.build:gradle:7.1.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.40.1")
    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
}