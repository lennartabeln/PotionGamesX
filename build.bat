@echo off
REM Build script for PotionGames - suppresses Maven internal warnings
setlocal enabledelayedexpansion

set MAVEN_OPTS=-XX:+IgnoreUnrecognizedVMOptions --add-opens=java.base/sun.misc=ALL-UNNAMED

echo.
echo [PotionGames Build Script]
echo Building with clean compilation...
echo.

mvn -DskipTests clean package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Build completed successfully!
    echo JAR: target\PotionGamesX-1.0.0.jar
) else (
    echo.
    echo [ERROR] Build failed with exit code %ERRORLEVEL%
)

endlocal
pause
