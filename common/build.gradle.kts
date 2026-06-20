plugins {
    id("java")
}

val gitBranch: String by rootProject.extra
version = "${providers.gradleProperty("common_version").get()}-$gitBranch"

base {
    archivesName.set("input-engine-common")
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.10.1")
}
