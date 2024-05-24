import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.joeloewi.croissant.configureAndroidCompose
import com.joeloewi.croissant.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply(libs.findPlugin("jetbrainsCompose").get().get().pluginId)
                apply(libs.findPlugin("compose.compiler").get().get().pluginId)
            }
            val extension = extensions.getByType<BaseAppModuleExtension>()
            configureAndroidCompose(extension)

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                enableStrongSkippingMode.set(true)
            }
        }
    }
}