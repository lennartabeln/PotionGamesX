@REM Suppress sun.misc.Unsafe warnings from Maven's internal dependencies
set MAVEN_OPTS=-XX:+IgnoreUnrecognizedVMOptions --add-opens=java.base/sun.misc=ALL-UNNAMED
