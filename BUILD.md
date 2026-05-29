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
export MAVEN_OPTS="--add-opens java.base/sun.misc=ALL-UNNAMED -XX:+IgnoreUnrecognizedVMOptions"
mvn -DskipTests clean package
```

## Output

- **JAR File**: `target/PotionGamesX-1.0.0.jar` (0.31 MB)
- **Code Warnings**: 0
- **Code Errors**: 0

## Known Notes

### JVM/Maven Warnings (Safe to Ignore)

When building, you may see warnings about `sun.misc.Unsafe` from Google Guice (Maven's internal dependency):

```
WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::staticFieldBase has been called by 
  com.google.inject.internal.aop.HiddenClassDefiner
```

**✅ These warnings are SAFE and do NOT affect the plugin:**
- They come from Maven's internal `guice-5.1.0` library
- NOT from PotionGames code
- The plugin compiles with **0 code warnings**
- The Unsafe methods are used by Google's dependency injection framework

**Suppression:**
- Use provided build scripts (build.ps1, build.bat) - they automatically suppress these
- Or set MAVEN_OPTS: `--add-opens java.base/sun.misc=ALL-UNNAMED`

**Future Java Versions:**
- When Java removes sun.misc.Unsafe (planned for future release), Maven/Guice maintainers will update
- PotionGames will not be affected as we don't use Unsafe directly

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
