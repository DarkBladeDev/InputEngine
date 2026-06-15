plugins {
	id("net.fabricmc.fabric-loom-remap")
}

base {
    archivesName.set("input-engine-fabric")
}

dependencies {
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	mappings("net.fabricmc:yarn:${providers.gradleProperty("yarn_mappings").get()}:v2")
	modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
    
    implementation(project(":common"))
}

tasks.processResources {
	val version = project.version.toString()
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<Jar> {
    from(project(":common").sourceSets.main.get().output)
}
