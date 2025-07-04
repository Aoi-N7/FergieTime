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
        // jitpack.ioは一時的にコメントアウト
        // maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FergieTime"
include(":app")
