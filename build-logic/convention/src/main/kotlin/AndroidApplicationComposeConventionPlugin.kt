import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.joeloewi.croissant.configureAndroidCompose
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
            }
            val extension = extensions.getByType<BaseAppModuleExtension>()
            configureAndroidCompose(extension)

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                enableStrongSkippingMode.set(true)
            }
        }
    }
}