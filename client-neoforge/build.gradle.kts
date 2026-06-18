plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.141"
}

val gitBranch: String by rootProject.extra
version = "${providers.gradleProperty("neoforge_version").get()}-$gitBranch"

base {
    archivesName.set("InputEngine-neoforge")
}

neoForge {
    version = providers.gradleProperty("neoforge_loader_version").get()


    mods {
        create("input_engine") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    implementation(project(":common"))
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("version", project.version)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.register<JavaExec>("runCheckPD") {
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.darkblade.mod.input_engine.client.CheckPD")
}

tasks.withType<Jar> {
    from(project(":common").sourceSets.main.get().output)
}
