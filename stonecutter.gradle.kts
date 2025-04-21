plugins {
    `maven-publish`
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false

    id("me.modmuss50.mod-publish-plugin") version "0.7.4" apply false // https://github.com/modmuss50/mod-publish-plugin
    id("dev.ithundxr.silk") version "0.11.+" // https://github.com/IThundxr/silk
}

stonecutter active "1.20.1" /* [SC] DO NOT EDIT */
//stonecutter.automaticPlatformConstants = true

stonecutter.parameters {
    // With flat-arch setup, where versions are in versions/{version}-{loader}:
    val loader = metadata.project.substringAfterLast("-")
    consts(loader, "fabric", "neoforge", "forge")
}

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter registerChiseled tasks.register("chiseledPublish", stonecutter.chiseled) {
    group = "project"
    ofTask("publish")
}

for (it in stonecutter.tree.branches) {
    if (it.id.isEmpty()) continue
    val loader = it.id.upperCaseFirst()

    // Builds loader-specific versions into `build/libs/{mod.version}/{loader}`
    stonecutter registerChiseled tasks.register("chiseledBuild$loader", stonecutter.chiseled) {
        group = "project"
        versions { branch, _ -> branch == it.id }
        ofTask("buildAndCollect")
    }

    // Publishes loader-specific versions
    stonecutter registerChiseled tasks.register("chiseledPublish$loader", stonecutter.chiseled) {
        group = "project"
        versions { branch, _ -> branch == it.id }
        ofTask("publish")
    }
}

// Runs active versions for each loader
for (it in stonecutter.tree.nodes) {
    if (it.metadata != stonecutter.current || it.branch.id.isEmpty()) continue
    val types = listOf("Client", "Server")
    val loader = it.branch.id.upperCaseFirst()
    for (type in types) it.project.tasks.register("runActive$type$loader") {
        group = "project"
        dependsOn("run$type")
    }
}
subprojects {
    apply(plugin = "maven-publish")
    repositories {
        mavenCentral()
        // mappings
        maven("https://maven.parchmentmc.org")

        maven("https://maven.createmod.net") // Create, Ponder, Flywheel
        maven("https://maven.tterrag.com/")
        maven("https://maven.shedaniel.me/")
        maven("https://maven.blamejared.com/")

        exclusiveContent {
            forRepository {
                maven {
                    name = "Modrinth"
                    url = uri("https://api.modrinth.com/maven")
                }
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
        exclusiveContent {
            forRepository {
                maven {
                    name = "CurseMaven"
                    url = uri("https://cursemaven.com")
                }
            }
            filter {
                includeGroup("curse.maven")
            }
        }
        flatDir{
            dir("libs")
        }
    }
}
