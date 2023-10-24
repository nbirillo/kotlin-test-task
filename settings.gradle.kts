rootProject.name = "Kotlin_test_task"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://packages.jetbrains.team/maven/p/kotlin-test-framework/kotlin-test-framework")
    }
}

include(
    "tamagochiServer"
)
