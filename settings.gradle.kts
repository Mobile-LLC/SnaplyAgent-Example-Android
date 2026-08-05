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
        // Where the Snaply SDK comes from. Nothing else is needed — no credentials, no private
        // repository, no token in your Gradle files.
        mavenCentral()
    }
}

rootProject.name = "SnaplyAgentExample"
include(":app")
