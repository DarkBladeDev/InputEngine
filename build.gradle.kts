plugins {
	id("java")
}

allprojects {
	group = providers.gradleProperty("maven_group").get()

	repositories {
		mavenCentral()
		maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
		maven("https://oss.sonatype.org/content/repositories/snapshots")
		maven("https://oss.sonatype.org/content/repositories/central")
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
