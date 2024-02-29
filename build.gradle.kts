buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.gms.oss.licenses.gradlePlugin)
        classpath(libs.kotlinx.atomicfu.gradlePlugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.ksp) apply false
}