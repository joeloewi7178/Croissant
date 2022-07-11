plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.joeloewi.croissant"
        minSdk = 21
        targetSdk = 32
        versionCode = 14
        versionName = "1.0.14"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    namespace = "com.joeloewi.croissant"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("com.google.android.material:material:1.7.0-alpha02")
    implementation("androidx.compose.material3:material3:${Versions.material3}")
    implementation("androidx.compose.material3:material3-window-size-class:${Versions.material3}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
    implementation("androidx.activity:activity-compose:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")

    //hilt
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    //hilt-extension
    implementation("androidx.hilt:hilt-work:${Versions.hiltExtension}")
    kapt("androidx.hilt:hilt-compiler:${Versions.hiltExtension}")

    //compose
    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.compose.foundation:foundation:${Versions.compose}")
    implementation("androidx.navigation:navigation-compose:2.5.0")
    implementation("androidx.compose.runtime:runtime:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")
    implementation("androidx.compose.material:material-icons-extended:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    //accompanist
    implementation("com.google.accompanist:accompanist-permissions:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-pager:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-placeholder:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-webview:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}")

    //work
    implementation("androidx.work:work-runtime-ktx:${Versions.work}")

    //start up
    implementation("androidx.startup:startup-runtime:${Versions.startup}")

    //splashscreen
    implementation("androidx.core:core-splashscreen:1.0.0-rc01")

    //image load
    implementation("io.coil-kt:coil-compose:${Versions.coil}")

    //webkit
    implementation("androidx.webkit:webkit:1.4.0")

    //paging
    implementation("androidx.paging:paging-compose:1.0.0-alpha15")

    //java.time back porting
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.0")

    // https://mvnrepository.com/artifact/com.google.android.material/compose-theme-adapter-3
    implementation("com.google.android.material:compose-theme-adapter-3:1.0.14")

    //html parsing
    implementation("org.jsoup:jsoup:1.15.2")

    //in-app update
    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.play:core-ktx:1.8.1")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:30.2.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    //leakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}