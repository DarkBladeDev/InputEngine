pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven {
			name = "NeoForge"
			url = uri("https://maven.neoforged.net/releases")
		}
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version")
	}
}

rootProject.name = "input-engine"

include("common")
include("client-fabric")
include("client-neoforge")
include("plugin-spigot")
include("example-plugin")
