pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Fiddle start - Project setup - Check for Git
if (!file(".git").exists()) {
    val errorText = """
        
        =====================[ ERROR ]=====================
         The Fiddle project directory is not a properly cloned Git repository.
         
         In order to build Fiddle from source you must clone
         the Fiddle repository using Git, not download a code
         zip from GitHub.
         
         Built Fiddle jars are available for download at
         https://github.com/FiddleMC/Fiddle/actions
         
         See https://github.com/PaperMC/Paper/blob/master/CONTRIBUTING.md
         for further information on building and modifying Paper forks.
        ===================================================
    """.trimIndent()
    error(errorText)
}
// Fiddle end - Project setup - Check for Git

// Fiddle start - Set up Gradle project
rootProject.name = "fiddle"

include("fiddle-api")
include("fiddle-server")
// Fiddle end - Set up Gradle project

optionalInclude("test-plugin")

fun optionalInclude(name: String, op: (ProjectDescriptor.() -> Unit)? = null) {
    val settingsFile = file("$name.settings.gradle.kts")
    if (!settingsFile.exists()) {
        settingsFile.writeText(
            """
            // Comment to disable the '$name' project
            include(":$name")

            """.trimIndent()
        )
    }
    apply(from = settingsFile)
    findProject(":$name")?.let { op?.invoke(it) }
}
