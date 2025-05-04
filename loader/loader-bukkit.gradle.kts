plugins {
    java
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "pl.skidamek"
version = project.findProperty("mod_version") ?: "unspecified"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "commander"
        url = uri("https://dl.cloudsmith.io/qshTFUucaaD2Gctc/lilbrocodes/commander/maven/")
    }
}

dependencies {
    implementation(project(":core"))

    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")

    implementation("org.lilbrocodes:commander:1.131")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.71")
    implementation("io.netty:netty-all:4.1.118.Final")
    implementation("org.tomlj:tomlj:1.1.1")
    implementation("com.github.luben:zstd-jni:1.5.7-1")
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName.set("automodpack-bukkit-${project.version}.jar")

    // Optional: Uncomment to customize dependencies
    /*
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("org.apache.logging.log4j:log4j-core"))
        exclude(dependency("org.jetbrains:annotations"))
        exclude(dependency("io.netty:netty-all"))
        exclude(dependency("org.tomlj:tomlj"))
        exclude(dependency("com.github.luben:zstd-jni"))
        include(dependency("org.bouncycastle:bcpkix-jdk18on"))
    }
    */
}

tasks.named<xyz.jpenilla.runpaper.task.RunServer>("runServer") {
    version.set("1.20.1")
}
