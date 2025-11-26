import org.gradle.kotlin.dsl.mavenCentral
import org.gradle.kotlin.dsl.repositories

include(":common_libs")


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
