@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.croissant.android.hilt)
    alias(libs.plugins.croissant.android.room)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ksp)
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    namespace = "com.joeloewi.croissant.data"
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.espresso.core)

    //retrofit2
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.scalars)
    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.okhttp3.dnsoverhttps)

    //moshi
    implementation(libs.moshi)
    implementation(libs.moshi.adapters)
    ksp(libs.moshi.kotlin.codegen)

    //protobuf
    implementation(libs.protobuf.javalite)
    implementation(libs.protobuf.kotlin.lite)

    //datastore
    implementation(libs.androidx.dataStore.core)

    implementation(libs.sandwich)
    implementation(libs.sandwich.retrofit)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.guava)

    implementation(libs.androidx.startup)

    implementation(libs.androidx.lifecycle.process)
}

protobuf {

    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                val java by registering {
                    option("lite")
                }
                val kotlin by registering {
                    option("lite")
                }
            }
        }
    }
}