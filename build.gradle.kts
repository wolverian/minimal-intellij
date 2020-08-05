import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML

plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    id("org.jetbrains.changelog") version "0.4.0"
}

group = "me.ii"
version = "0.0.2"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    jcenter()
}

dependencies {
}

// Import variables from gradle.properties file
val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.2"
}

tasks {
    patchPluginXml {
        version(pluginVersion)
        sinceBuild(pluginSinceBuild)
        untilBuild(pluginUntilBuild)
        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription(closure {
            File("./README.md").readText().lines().run {
                subList(indexOf("<!-- Plugin description -->") + 1, indexOf("<!-- Plugin description end -->"))
            }.joinToString("\n").run { markdownToHTML(this) }
        })
        changeNotes(changelog.getLatest().toHTML())
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
        channels(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
    }
}