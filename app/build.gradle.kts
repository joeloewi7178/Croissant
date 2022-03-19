plugins {
    id("com.android.application")
    `kotlin-android`
    `kotlin-kapt`
    `kotlin-parcelize`
    id("dagger.hilt.android.plugin")
}

android {
    defaultConfig {
        compileSdk = 32
        applicationId = "com.joeloewi.croissant"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true",
                        "room.expandProjection" to "true"
                    )
                )
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            isDebuggable = false
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
        kotlinCompilerExtensionVersion = Versions.compose
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.compose.material3:material3:1.0.0-alpha07")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}")
    implementation("androidx.activity:activity-compose:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}")

    //moshi
    implementation("com.squareup.moshi:moshi:${Versions.moshi}")
    implementation("com.squareup.moshi:moshi-adapters:${Versions.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}")

    //retrofit2
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-scalars:${Versions.retrofit}")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    //hilt
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    //hilt-extension
    implementation("androidx.hilt:hilt-work:${Versions.hiltExtension}")
    kapt("androidx.hilt:hilt-compiler:${Versions.hiltExtension}")

    //compose
    implementation("androidx.navigation:navigation-compose:2.4.1")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")
    implementation("androidx.compose.material:material-icons-extended:${Versions.compose}")

    //accompanist
    implementation("com.google.accompanist:accompanist-permissions:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-pager:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-placeholder:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-webview:${Versions.accompanist}")
    implementation("com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}")

    //work
    implementation("androidx.work:work-runtime-ktx:${Versions.work}")

    //start up
    implementation("androidx.startup:startup-runtime:${Versions.startup}")

    //room
    implementation("androidx.room:room-runtime:${Versions.room}")
    kapt("androidx.room:room-compiler:${Versions.room}")
    implementation("androidx.room:room-ktx:${Versions.room}")
    implementation("androidx.room:room-paging:${Versions.room}")

    //splashscreen
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")

    //image load
    implementation("io.coil-kt:coil-compose:${Versions.coil}")

    implementation("androidx.webkit:webkit:1.4.0")
}

kapt {
    correctErrorTypes = true
}