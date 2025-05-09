@file:Suppress("UnstableApiUsage")

import dev.ithundxr.silk.ChangelogText

plugins {
    `maven-publish`
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.ithundxr.silk")
}

val loader = prop("loom.platform")!!
val loaderCap = loader.upperCaseFirst()
val minecraft: String = stonecutter.current.version
val common: Project = requireNotNull(stonecutter.node.sibling("")) {
    "No common project for $project"
}.project

val ci = System.getenv("CI")?.toBoolean() ?: false
val release = System.getenv("RELEASE")?.toBoolean() ?: false
val nightly = ci && !release
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()

version = "${mod.version}${if (release) "" else "-dev"}+mc.${minecraft}-${loader}${if (nightly) "-build.${buildNumber}" else ""}"
group = "${mod.group}.$loader"
base {
    archivesName.set(mod.id)
}

architectury {
    platformSetupLoomIde()
    forge()
}

val commonBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations {
    compileClasspath.get().extendsFrom(commonBundle)
    runtimeClasspath.get().extendsFrom(commonBundle)
    get("developmentForge").extendsFrom(commonBundle)
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    forge.convertAccessWideners = true
//    forge.mixinConfigs(
//        "${mod.id}-common.mixins.json",
//        "${mod.id}-forge.mixins.json",
//    )

    runConfigs.all {
        isIdeConfigGenerated = true
        runDir = "../../../run"
        vmArgs("-Dmixin.debug.export=true")
    }
}

repositories {
    maven("https://maven.minecraftforge.net")
}

dependencies {
    commonBundle(project(common.path, "namedElements")) { isTransitive = false }
    shadowBundle(project(common.path, "transformProductionForge")) { isTransitive = false }

    minecraft("com.mojang:minecraft:$minecraft")

    mappings(loom.layered {
        officialMojangMappings { nameSyntheticMembers = false }
        parchment("org.parchmentmc.data:parchment-${minecraft}:${common.mod.dep("parchment_version")}@zip")
    })

    "forge"("net.minecraftforge:forge:$minecraft-${common.mod.dep("forge_loader")}")

    "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }

    modImplementation("com.simibubi.create:create-${minecraft}:${common.mod.dep("create_forge")}:slim") { isTransitive = false }
    modImplementation("com.tterrag.registrate:Registrate:${common.mod.dep("registrate_forge")}")
    modImplementation("dev.engine-room.flywheel:flywheel-forge-${minecraft}:${common.mod.dep("flywheel_forge")}")
    compileOnly("dev.engine-room.flywheel:flywheel-forge-api-${minecraft}:${common.mod.dep("flywheel_forge")}")
    modImplementation("net.createmod.ponder:Ponder-Forge-${minecraft}:${common.mod.dep("ponder_forge")}")
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.remapJar {
    injectAccessWidener = true
    input = tasks.shadowJar.get().archiveFile
    archiveClassifier = null
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    exclude("fabric.mod.json", "architectury.common.json")
}

tasks.processResources {
    properties(listOf("META-INF/mods.toml", "pack.mcmeta"),
        "id" to mod.id, "name" to mod.name, "license" to mod.license,
        "version" to mod.version, "minecraft" to common.mod.prop("mc_dep_forgelike"),
        "authors" to mod.authors, "description" to mod.description
    )
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

tasks.register<Copy>("buildAndCollect") {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
    from(tasks.remapJar.get().archiveFile, tasks.remapSourcesJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}/$loader"))
    dependsOn("build")
}

// Modmuss Publish
publishMods {
    file = tasks.remapJar.get().archiveFile
    changelog = ChangelogText.getChangelogText(rootProject).toString()
    displayName = "${common.mod.version} for $loaderCap $minecraft"
    modLoaders.addAll("forge", "neoforge")
    type = ALPHA

    curseforge {
        projectId = "publish.curseforge"
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        minecraftVersions.add(minecraft)

        requires {

        }
    }

    modrinth {
        projectId = "publish.modrinth"
        accessToken = System.getenv("MODRINTH_TOKEN")
        minecraftVersions.add(minecraft)

        requires {

        }
    }

    dryRun = System.getenv("DRYRUN")?.toBoolean() ?: true
}
