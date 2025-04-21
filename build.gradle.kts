@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("me.modmuss50.mod-publish-plugin")
    id("dev.ithundxr.silk")
}

val minecraft = stonecutter.current.version

val ci = System.getenv("CI")?.toBoolean() ?: false
val release = System.getenv("RELEASE")?.toBoolean() ?: false
val nightly = ci && !release
val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()

version = "${mod.version}${if (release) "" else "-dev"}+mc.${minecraft}-common${if (nightly) "-build.${buildNumber}" else ""}"
group = "${group}.common"
base.archivesName.set(mod.id)

architectury.common(stonecutter.tree.branches.mapNotNull {
    if (stonecutter.current.project !in it) null
    else it.project.prop("loom.platform")
})
loom {
    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

repositories {
    maven("https://mvn.devos.one/snapshots/") // Create Fabric
    maven("https://mvn.devos.one/releases/") // Porting Lib
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Forge Config API Port
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    mappings(loom.layered {
        officialMojangMappings { nameSyntheticMembers = false }
        parchment("org.parchmentmc.data:parchment-${minecraft}:${mod.dep("parchment_version")}@zip")
    })

    // fabric stuffs
    modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_api_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${mod.dep("fabric_api_version")}")

    // general stuff
    "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
        annotationProcessor(it)
        implementation(it)
    }

    if (stonecutter.eval(stonecutter.current.version, "<1.21.1"))
        modImplementation("com.simibubi.create:create-fabric-${minecraft}:${mod.dep("create_fabric")}")
    return@dependencies
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.build {
    group = "versioned"
    description = "Must run through 'chiseledBuild'"
}

// Global control over Modmuss Publish
tasks.create("templatePublish") {
    when (val platform = System.getenv("PLATFORM")) {
        "all" -> {
            dependsOn(tasks.build,
                ":fabric:publish", ":forge:publish", ":neoforge:publish", ":common:publish",
                ":fabric:publishMods", ":forge:publishMods", ":neoforge:publishMods")
        }
        "fabric", "forge", "neoforge" -> {
            dependsOn("${platform}:build", "${platform}:publish", "${platform}:publishMods")
        }
    }
}
