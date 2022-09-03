@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("croissant.android.application")
    id("croissant.android.application.compose")
    kotlin("kapt")
    alias(libs.plugins.gms.google.services)
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.joeloewi.croissant"

    defaultConfig {
        applicationId = "com.joeloewi.croissant"
        versionCode = 21
        versionName = "1.0.21"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        val debug by getting {

        }
        val release by getting {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.android.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.androidx.compose.ui.test)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    implementation(libs.androidx.hilt.navigation.compose)

    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    //hilt-extension
    implementation(libs.hilt.ext.work)
    kapt(libs.hilt.ext.compiler)

    //compose
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.material.iconsExtended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //accompanist
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.placeholder)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.webview)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.swiperefresh)

    //work
    implementation(libs.androidx.work.ktx)

    //start up
    implementation(libs.androidx.startup)

    //splashscreen
    implementation(libs.androidx.core.splashscreen)

    //image load
    implementation(libs.coil.kt.compose)

    //webkit
    implementation(libs.androidx.webkit)

    //paging
    implementation(libs.androidx.paging.compose)

    // https://mvnrepository.com/artifact/com.google.android.material/compose-theme-adapter-3
    implementation(libs.material.compose.theme.adapter3)

    //html parsing
    implementation(libs.jsoup)

    //in-app update
    implementation(libs.android.play.core)
    implementation(libs.android.play.core.ktx)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    //leakCanary
    debugImplementation(libs.leakcanary.android)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.savedstate.ktx)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}