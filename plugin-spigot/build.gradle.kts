plugins {
    id("java")
}

version = providers.gradleProperty("spigot_version").get()

base {
    archivesName.set("input-engine-spigot")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks.processResources {
	val version = project.version.toString()
	inputs.property("version", version)

	filesMatching("plugin.yml") {
		expand("version" to version)
	}
}

tasks.withType<Jar> {
    from(project(":common").sourceSets.main.get().output)
}
