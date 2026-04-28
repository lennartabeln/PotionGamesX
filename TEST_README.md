# PotionGames Plugin Testing Guide

## Overview
This document describes the comprehensive testing infrastructure added to the PotionGames plugin, including error handling and unit tests.

## Error Handling System

### Components

#### 1. ErrorContext (`error/ErrorContext.java`)
A flexible error context builder that enables constructing errors with different message combinations.

**Features:**
- Builder pattern for readable error creation
- Three-tier messaging: User, Admin, and Console
- Error levels: INFO, WARNING, ERROR, CRITICAL
- Exception tracking and logging support

**Usage:**
```java
// Create an error context
ErrorContext error = new ErrorContext.Builder("ERROR_CODE")
    .userMessage("Something went wrong")
    .adminMessage("Detailed technical info")
    .consoleMessage("Full debug info")
    .level(ErrorContext.ErrorLevel.ERROR)
    .exception(new RuntimeException("cause"))
    .build();

// Send to player
error.sendToPlayer(player);

// Send to admin only
error.sendToAdmin(player); // checks permission pg.admin

// Log to console
error.logToConsole(logger);
```

#### 2. PotionGamesError (`error/PotionGamesError.java`)
Predefined error codes and messages organized by category.

**Error Categories:**
- **CFG_*** : Configuration errors (load, save, invalid values)
- **DB_*** : Database errors (connection, queries, inserts)
- **GAME_*** : Game state errors (not running, full, lobby issues)
- **PLAYER_*** : Player state errors (not found, not in game, insufficient count)
- **CMD_*** : Command errors (invalid syntax, args, permissions)
- **SETUP_*** : Arena setup errors (invalid locations, duplicates)
- **ECON_*** : Economy/reward errors (Vault not found, insufficient funds)
- **SYS_*** : System errors (initialization, state transitions)

**Current Predefined Errors:**
```
CONFIG_LOAD_FAILED       - CFG_001
CONFIG_INVALID           - CFG_002
CONFIG_SAVE_FAILED       - CFG_003
DB_CONNECTION_FAILED     - DB_001
DB_QUERY_FAILED          - DB_002
DB_INSERT_FAILED         - DB_003
DB_CLOSE_FAILED          - DB_004
GAME_NOT_RUNNING         - GAME_001
GAME_FULL                - GAME_002
GAME_IN_PROGRESS         - GAME_003
LOBBY_NOT_FOUND          - GAME_004
ARENA_NOT_FOUND          - GAME_005
PLAYER_NOT_FOUND         - PLAYER_001
PLAYER_NOT_IN_GAME       - PLAYER_002
PLAYER_ALREADY_IN_GAME   - PLAYER_003
INSUFFICIENT_PLAYERS     - PLAYER_004
INVALID_COMMAND          - CMD_001
INVALID_ARGS             - CMD_002
PERMISSION_DENIED        - CMD_003
COMMAND_EXECUTION_FAILED - CMD_004
SETUP_SPAWN_INVALID      - SETUP_001
SETUP_DUPLICATE_ARENA    - SETUP_002
SETUP_NO_SPAWNS          - SETUP_003
VAULT_NOT_FOUND          - ECON_001
INSUFFICIENT_FUNDS       - ECON_002
PLUGIN_INITIALIZATION    - SYS_001
INVALID_STATE            - SYS_002
UNKNOWN_ERROR            - SYS_999
```

#### 3. ErrorHandler (`error/ErrorHandler.java`)
Central facade for consistent error reporting across the plugin.

**Key Methods:**
```java
// Handle predefined errors
errorHandler.handle(PotionGamesError.GAME_FULL);

// Handle custom errors
errorHandler.handle("CODE", "user msg", "admin msg", exception);

// Send to specific players
errorHandler.sendToPlayer(player, PotionGamesError.GAME_FULL);
errorHandler.sendToPlayer(player, "Custom message");

// Convenience methods
errorHandler.sendSuccess(player, "Action completed");
errorHandler.sendInfo(player, "Information");
errorHandler.sendWarning(player, "Warning");

// Console logging
errorHandler.logError("CODE", "message", exception);
errorHandler.logWarning("CODE", "message");
errorHandler.logInfo("message");
```

## Testing Infrastructure

### Test Framework
- **JUnit 4**: Unit testing framework
- **Mockito**: Mock object creation and verification
- **Maven Surefire**: Test runner plugin

### Running Tests

**Run all tests:**
```bash
mvn test
```

**Run specific test class:**
```bash
mvn test -Dtest=ErrorContextTest
```

**Run with coverage (requires plugin):**
```bash
mvn test jacoco:report
```

### Test Organization

Tests are organized to mirror source structure:
```
src/test/java/com/tw0far/potiongames/
├── error/
│   ├── ErrorContextTest.java       # Error context builder tests
│   ├── PotionGamesErrorTest.java   # Predefined error enum tests
│   └── ErrorHandlerTest.java       # Error handler facade tests
└── util/
    ├── ItemBuilderTest.java        # Item builder tests
    ├── MessageUtilTest.java        # Message component tests
    └── LocationUtilTest.java       # Location utility tests
```

### Test Coverage

#### Error Handling Tests (12 tests)

**ErrorContextTest** - Tests ErrorContext builder pattern:
- ✓ Builder creates valid ErrorContext
- ✓ Exception tracking works correctly
- ✓ Default messages are populated
- ✓ All ErrorLevel values exist
- ✓ Console logging doesn't throw

**PotionGamesErrorTest** - Tests predefined errors:
- ✓ All error codes are unique
- ✓ All error messages populated (user & technical)
- ✓ Config errors exist and have messages
- ✓ Database errors exist and have messages
- ✓ Game errors exist and have messages
- ✓ Player errors exist and have messages
- ✓ Command errors exist and have messages
- ✓ Unknown error exists as fallback

**ErrorHandlerTest** - Tests error reporting:
- ✓ ErrorHandler instantiation
- ✓ Handling predefined errors
- ✓ Handling custom errors
- ✓ Handling errors with exceptions
- ✓ Logging errors to console
- ✓ Logging warnings
- ✓ Logging info messages
- ✓ Success method compatibility

#### Utility Tests (6 tests)

**MessageUtilTest** - Tests message component creation:
- ✓ MessageUtil class exists
- ✓ createInfo() returns non-null Component
- ✓ createWarning() returns non-null Component
- ✓ createError() returns non-null Component
- ✓ createSuccess() returns non-null Component

**LocationUtilTest** - Tests location utilities:
- ✓ LocationUtil class exists
- ✓ Locations have valid coordinates
- ✓ Locations have valid rotation (yaw/pitch)
- ✓ Location cloning works correctly

**ItemBuilderTest** - Tests item builder utilities:
- ✓ ItemBuilder class exists
- ✓ ItemStack instantiation works
- ✓ ItemStack with quantity works

### Integration with Managers

The error handling system is designed to be integrated into all manager classes:

**ConfigurationManager:**
```java
try {
    // Load config
} catch (IOException e) {
    errorHandler.handle("CFG_001", "Failed to load configuration", 
                       "IOException: " + e.getMessage(), e);
}
```

**DatabaseManager:**
```java
if (!connect()) {
    errorHandler.handle(PotionGamesError.DB_CONNECTION_FAILED);
    return;
}
```

**CommandDispatcher:**
```java
if (!player.hasPermission("pg.admin")) {
    errorHandler.sendToPlayer(player, PotionGamesError.PERMISSION_DENIED);
    return false;
}
```

## Message Tier Explanation

### User Messages
- **For**: Regular players
- **Style**: Simple, friendly, action-oriented
- **Example**: "Game is full. Try another lobby or wait for a slot."
- **Shows**: What happened and what to do next

### Admin Messages
- **For**: Server administrators (permission: `pg.admin`)
- **Style**: Technical but concise
- **Example**: "Player joined lobby_1 successfully. Current: 8/10 players"
- **Shows**: Additional context useful for debugging

### Console Messages
- **For**: Server logs
- **Style**: Detailed technical information
- **Example**: "GAME_002 - Player 'Steve' attempted join but game full. Queue size: 0"
- **Shows**: Full context including stack traces if exceptions occurred

## Error Level Guidelines

- **INFO**: General information, not an error
- **WARNING**: Something unexpected but recoverable (e.g., player timeout)
- **ERROR**: Operation failed but plugin continues (e.g., query failed, retry possible)
- **CRITICAL**: Fatal issue, may require server restart (e.g., database corruption)

## Adding New Errors

To add new predefined errors:

1. **Add to PotionGamesError enum:**
```java
NEW_ERROR("CODE_###", "User message", "Technical message")
```

2. **Use in code:**
```java
errorHandler.sendToPlayer(player, PotionGamesError.NEW_ERROR);
```

3. **Add test case:**
```java
@Test
public void testNewErrorExists() {
    assertEquals("CODE_###", PotionGamesError.NEW_ERROR.getCode());
}
```

## Future Enhancements

1. **Additional Test Coverage**: Create tests for managers and commands
2. **Integration Tests**: End-to-end testing of game flows
3. **Performance Tests**: Verify error handling doesn't impact performance
4. **Internationalization**: Support error messages in multiple languages
5. **Error Metrics**: Track which errors occur most frequently
6. **Graceful Degradation**: Error recovery strategies per error type

## Maven Configuration

The pom.xml includes testing dependencies:

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

To run tests: `mvn test`
