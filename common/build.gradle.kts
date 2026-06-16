plugins {
    id("java")
}

version = providers.gradleProperty("common_version").get()

base {
    archivesName.set("input-engine-common")
}
