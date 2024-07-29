import com.joeloewi.croissant.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }
            dependencies {
                "ksp"(libs.findLibrary("hilt.compiler").get())
                "ksp"(libs.findLibrary("hilt.ext.compiler").get())
                "implementation"(libs.findLibrary("hilt.core").get())
            }

            pluginManager.withPlugin("com.android.base") {
                with(pluginManager) {
                    apply("dagger.hilt.android.plugin")
                    dependencies {
                        "implementation"(libs.findLibrary("hilt.android").get())
                    }
                }
            }
        }
    }
}