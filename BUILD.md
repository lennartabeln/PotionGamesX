# Building PotionGames

## Quick Build

```bash
mvn -DskipTests clean package
```

## Build Scripts

Use the provided build scripts to build with optimal settings:

### Windows (PowerShell)
```powershell
.\build.ps1
```

### Windows (Batch)
```cmd
build.bat
```

### Linux/Mac
```bash
export MAVEN_OPTS="-XX:+IgnoreUnrecognizedVMOptions --add-opens=java.base/sun.misc=ALL-UNNAMED"
mvn -DskipTests clean package
```

## Output

- **JAR File**: `target/PotionGamesX-1.0.0.jar` (0.31 MB)
- **Code Warnings**: 0
- **Code Errors**: 0

## Known Notes

### JVM/Maven Warnings
The build may show warnings about `sun.misc.Unsafe` from Google Guice (Maven internal dependency). These are NOT code warnings and do not affect the plugin:

```
WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::staticFieldBase has been called by 
  com.google.inject.internal.aop.HiddenClassDefiner
```

**These warnings are from Maven's internal build tools, not from PotionGames code.**

All PotionGames code compiles with **0 warnings**.

## Deployment

1. Build the plugin: `mvn -DskipTests clean package`
2. Copy JAR to server: `plugins/PotionGamesX-1.0.0.jar`
3. Restart server: `/restart`
4. Verify loading: Check console for `[PotionGames]` messages

## Troubleshooting

If you see compilation errors:
- Ensure Java 25+ is installed: `java -version`
- Clear Maven cache: `mvn clean`
- Update dependencies: `mvn dependency:resolve`
