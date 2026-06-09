## Essential Commands
- `mvn clean install` to build the project (required before any tests or execution)
- `./gradlew test` to run tests (uses Gradle; package.json shows no Node.js dependencies)
- `java -jar target/PotionGamesX-1.0.0.jar` to launch the game (found in target directory)

## Build/Execution Workflow
- Maven-based project: always run `mvn clean` before `mvn install` to purge build artifacts
- Tests in `src/test` require JVM setup (no Node runtime needed)
- Game executable path is fixed to `target/PotionGamesX-1.0.0.jar`

## Repo-Specific Quirks
- Plugin configuration in `src/main/resources/plugin.yml` requires `mvn package` to generate JAR
- CI/CD triggers `Gradle Wrapper` (`./gradlew` command) for all builds
- No official documentation for deployment; infrastructure flow inferred from `target/classes/config.yml`

## Critical Paths
- Development: `mvn compile` -> `mvn test` -> `java -jar target/PotionGamesX-1.0.0.jar`
- Testing: Skip `mvn package` if only running unit tests
- Failure Handling: `mvn clean` always resolves transient build errors