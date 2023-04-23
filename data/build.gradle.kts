@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("croissant.android.library")
    id("croissant.android.hilt")
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ksp)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-proguard-rules.pro")

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
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

    //room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)

    //retrofit2
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.scalars)
    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3.logging.interceptor)

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

    implementation(libs.kotlinx.coroutines.android)
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

hilt {
    enableAggregatingTask = true
}