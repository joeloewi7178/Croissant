package com.joeloewi.croissant

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 34

        defaultConfig {
            minSdk = 21
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            isCoreLibraryDesugaringEnabled = true
        }

        kotlin {
            compilerOptions {
                // Treat all Kotlin warnings as errors (disabled by default)
                allWarningsAsErrors.set(properties["warningsAsErrors"] as? Boolean ?: false)

                freeCompilerArgs.set(
                    freeCompilerArgs.get().toMutableList() + listOf(
                        "-opt-in=kotlin.RequiresOptIn",
                        // Enable experimental coroutines APIs, including Flow
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=kotlinx.coroutines.FlowPreview",
                    )
                )

                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())

        val kotlinBom = libs.findLibrary("kotlin-bom").get()
        val kotlinStdlib = libs.findLibrary("kotlin-stdlib").get()
        "implementation"(platform(kotlinBom))
        "implementation"(kotlinStdlib)
        "androidTestImplementation"(platform(kotlinBom))

        val kotlinxCoroutinesBom = libs.findLibrary("kotlinx-coroutines-bom").get()
        "implementation"(platform(kotlinxCoroutinesBom))
        "androidTestImplementation"(platform(kotlinxCoroutinesBom))
    }
}

fun Project.kotlin(block: KotlinAndroidProjectExtension.() -> Unit) {
    extensions.configure<KotlinAndroidProjectExtension>(block)
}