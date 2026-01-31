pluginManagement {
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

rootProject.name = "SafeCoin"
include(":app")
include(":main_screen")
include(":common_libs")
//include(":features")
include(":features:exchanges")
include(":features:transactions")
