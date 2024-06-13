plugins {
    alias(libs.plugins.croissant.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.joeloewi.croissant.core.datastore.proto"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
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

dependencies {
    api(libs.protobuf.kotlin.lite)
}