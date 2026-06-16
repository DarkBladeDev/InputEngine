plugins {
    id("java")
}

base {
    archivesName.set("input-engine-example")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly(project(":plugin-spigot"))
    compileOnly(project(":common"))
}

tasks.processResources {
	val version = project.version.toString()
	inputs.property("version", version)

	filesMatching("plugin.yml") {
		expand("version" to version)
	}
}
