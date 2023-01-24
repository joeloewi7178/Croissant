@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("croissant.android.application")
    id("croissant.android.application.compose")
    id("croissant.android.hilt")
    alias(libs.plugins.gms.google.services)
    id("kotlin-parcelize")
    alias(libs.plugins.firebase.crashlytics)
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "com.joeloewi.croissant"

    defaultConfig {
        applicationId = "com.joeloewi.croissant"
        versionCode = 32
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val release by creating {
            keyAlias = System.getenv("ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
            storeFile = file("../croissant_key_store.jks")
            storePassword = System.getenv("KEY_STORE_PASSWORD")
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
            signingConfig = signingConfigs.getByName("release")
        }
        val benchmark by creating {
            initWith(release)
            signingConfig = signingConfigs.getByName("release")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            proguardFiles("baseline-profiles-rules.pro")
        }
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
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.process)

    implementation(libs.androidx.savedstate.ktx)

    implementation(libs.androidx.hilt.navigation.compose)

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
    implementation(libs.accompanist.themeadapter.material3)
    implementation(libs.accompanist.navigation.material)

    //work
    implementation(libs.androidx.work.ktx)

    //start up
    implementation(libs.androidx.startup)

    //splashscreen
    implementation(libs.androidx.core.splashscreen)

    //image load
    implementation(libs.coil.kt.base)
    implementation(libs.coil.kt.compose)

    //webkit
    implementation(libs.androidx.webkit)

    //paging
    implementation(libs.androidx.paging.compose)

    //html parsing
    implementation(libs.jsoup)

    //in-app update
    implementation(libs.android.play.app.update.ktx)

    //in-app review
    implementation(libs.android.play.review.ktx)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    //leakCanary
    debugImplementation(libs.leakcanary.android)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.androidx.profileinstaller)

    implementation(libs.tts)

    //open source license activity
    implementation(libs.gms.play.services.oss.licenses)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}