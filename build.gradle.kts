import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

group = "com.arsoban"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.javacord:javacord:3.3.2")
    implementation("io.insert-koin:koin-core:3.1.5")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "DiscordServerClonerGUI"
            packageVersion = "1.0.0"
            description = "DiscordServerClonerGUI"
            vendor = "Arsoban"

            windows {
                menuGroup = "DiscordServerClonerGUI"
                iconFile.set(project.file("src/main/resources/images/clone.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/images/clone.png"))
            }
        }
    }
}