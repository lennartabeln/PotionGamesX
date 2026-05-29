@REM Suppress sun.misc.Unsafe warnings from Maven's internal dependencies
set MAVEN_OPTS=--add-opens java.base/sun.misc=ALL-UNNAMED -XX:+IgnoreUnrecognizedVMOptions -Dorg.slf4j.simpleLogger.defaultLogLevel=warn
