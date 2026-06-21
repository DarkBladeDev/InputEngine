plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

val gitBranch: String by rootProject.extra
version = "${providers.gradleProperty("spigot_version").get()}-$gitBranch"

base {
    archivesName.set("InputEngine-spigot")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.2.1")
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

tasks.named<Jar>("jar") {
    archiveClassifier.set("dev")
}

tasks.shadowJar {
    configurations = project.configurations.runtimeClasspath.map { setOf(it) }

    dependencies {
        // Only merge bStats into the final jar, no other dependencies
        exclude { it.moduleGroup != "org.bstats" }
    }

    // Remove classifier from jar name
    archiveClassifier.set("")

    // Relocate bStats into the plugin's package to avoid conflicts with other
    // plugins using bStats
    relocate("org.bstats", project.group.toString())
}


tasks.build { dependsOn(tasks.shadowJar) }