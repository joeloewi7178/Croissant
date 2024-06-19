pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Croissant"
include(":app")
include(":domain")
include(":baselineprofile")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:datastore-proto")
include(":core:model")
include(":core:network")
include(":core:common")
include(":core:system")
include(":feature:attendances")
include(":feature:redeemcodes")
include(":feature:settings")
