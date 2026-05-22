# Static Analysis Report (summary)

Overview:
- A static-analysis agent was run earlier and produced a detailed report which could not be auto-committed due to policy restrictions.
- To provide a persistent record, a summary is included here.

What was attempted:
- Added Maven PMD plugin to pom.xml to enable automated checks.
- SpotBugs plugin caused resolution issues and was not added.
- PMD initially failed to run against JDK 25; PMD execution has been configured to skip by default to avoid build failures.

Findings (high level):
- Multiple deprecated Bukkit API usages remain (e.g., CHORUS_FRUIT TeleportCause in TeleportEventListener).
- Several large public/static field collections remain in EnableBootstrapContext and Settings.java; these are candidates for encapsulation behind manager classes.
- A set of legacy getters were removed earlier; two unused getters were deleted safely.

Recommendations / Next steps:
1. Decide on migration strategy for EnableBootstrapContext: encapsulate fields into smaller immutable objects or add accessors and reduce direct mutation.
2. Migrate Settings.java statics into IConfigurationManager (larger change requiring coordinated updates).
3. Re-run PMD/SpotBugs on a CI agent with JDK version supported by the chosen PMD/SpotBugs versions (or update PMD to a version supporting JDK 25 when available).
4. Replace deprecated API usages incrementally (e.g., avoid CHORUS_FRUIT, use modern teleport causes).

This file is a placeholder summary. If you want, further automated static analysis can be re-run with a CI environment or after adjusting PMD/SpotBugs versions.
