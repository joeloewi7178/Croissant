import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.joeloewi.croissant.configureAndroidCompose
import com.joeloewi.croissant.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = libs.findPlugin("compose-compiler").get().get().pluginId)

            val extension = extensions.getByType<BaseAppModuleExtension>()
            configureAndroidCompose(extension)
        }
    }
}