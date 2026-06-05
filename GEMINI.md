# PotionGames

PotionGames is a Minecraft minigame plugin designed for Bukkit/Paper servers. It provides a survival-style minigame experience with custom loot tables, potion effects, and various game modes like deathmatch.

## Project Overview

* **Type**: Minecraft Plugin (Bukkit/Paper)
* **Language**: Java 25
* **Build System**: Maven

## Building and Running

The project is built using Maven.

* **Build**: `mvn clean package`
* **Artifact**: The generated plugin file will be in the `target/` directory (e.g., `target/PotionGamesX-1.0.0.jar`).
* **Running/Testing**:
    * This plugin is intended to run on a Minecraft server (Bukkit/Paper).
    * To test, copy the built `.jar` into the `plugins/` folder of a compatible server.
    * Dependencies (Multiverse-Core, Vault) must be present in the server's `plugins/` folder.

## Development Conventions

* **Structure**: Follows the standard Maven project structure (`src/main/java`, `src/main/resources`).
* **Code Style**:
    * Adhere to Java best practices.
    * Maintain idiomatic plugin structure (`main` class extending `JavaPlugin`, `listeners` for events, `commands` for interaction).
* **Testing**:
    * Uses JUnit 5 (as indicated by the Surefire/Failsafe plugins in `pom.xml`).
    * Integration tests are run via the `maven-failsafe-plugin`.

## Important Configuration Files

* `pom.xml`: Maven build configuration and dependencies.
* `src/main/resources/plugin.yml`: Plugin metadata, main class entry point, commands, and dependencies.
* `src/main/resources/config.yml`: Global plugin configuration.
* `src/main/resources/messages.yml`: Plugin language and message strings.
