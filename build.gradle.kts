plugins {
	id("java")
}

val gitBranch: String by extra {
    try {
        val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD").start()
        process.inputStream.bufferedReader().readText().trim().let {
            if (it == "HEAD" || it.isEmpty()) "unknown" else it
        }
    } catch (e: Exception) {
        "unknown"
    }
}


allprojects {
	group = providers.gradleProperty("maven_group").get()

	repositories {
		mavenCentral()
		maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
		maven("https://oss.sonatype.org/content/repositories/snapshots")
		maven("https://oss.sonatype.org/content/repositories/central")
		maven("https://repo.extendedclip.com/releases/") // PlaceholderAPI
	}

	tasks.withType<JavaCompile>().configureEach {
		options.release.set(21)
	}

	apply(plugin = "java")

	java {
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
}
