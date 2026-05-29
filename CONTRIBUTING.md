# Contributing to PotionGames

Thank you for your interest in contributing to PotionGames! This guide will help you get started.

## Getting Started

### Prerequisites
- Java 25+
- Maven 3.8+
- Paper API 1.26+ knowledge
- Git

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/andersspielen/PotionGames.git
   cd PotionGames
   ```

2. **Build locally**
   ```bash
   mvn -DskipTests clean package
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

## Code Style & Conventions

### Naming
- Classes: PascalCase (e.g., `GameServerCommand`)
- Methods: camelCase (e.g., `getActivePlayers()`)
- Constants: UPPER_SNAKE_CASE (e.g., `MAX_PLAYERS`)
- Variables: camelCase (e.g., `lobbyId`)

### Architecture Patterns

#### State Management
Always use **delegation methods**, never direct HashMap access:

```java
// ✅ CORRECT - Use delegation
String lobbyId = plugin.getGame().getPlayerLobby(player);

// ❌ WRONG - Direct HashMap access
String lobbyId = plugin.playerLobby.get(player);
```

#### Commands
All commands must implement `ICommand`:

```java
public class MyCommand implements ICommand {
    private final PotionGames plugin;
    
    public MyCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() { return "mycommand"; }
    
    @Override
    public String getPermission() { return "pg.mycommand"; }
    
    @Override
    public boolean requiresGameServer() { return true; }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Use delegation methods
        return true;
    }
    
    @Override
    public String getUsage() { return "/pg mycommand"; }
}
```

#### Event Listeners
Separate listener classes by domain:

```java
public class MyListener implements Listener {
    private final PotionGames plugin;
    
    public MyListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCustomEvent(CustomEvent event) {
        Player player = event.getPlayer();
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId != null) {
            // Use delegation methods
        }
    }
}
```

### Formatting
- Use 4 spaces for indentation
- Max 120 characters per line
- One class per file
- Use meaningful variable names
- Add comments only for complex logic

### Documentation
- Javadoc for public methods
- Comments for non-obvious logic
- Update CHANGELOG.md for new features
- Update DOCUMENTATION.md if needed

## File Structure

```
src/main/java/com/tw0far/potiongames/
├── models/          # Domain model classes
├── managers/        # State management
├── commands/        # Command handlers
├── listeners/       # Event handlers
├── handlers/        # Complex workflows
├── bootstrap/       # Initialization
├── util/            # Utilities
└── main/            # Plugin entry point
```

## Adding New Features

### Adding a Command

1. Create `src/main/java/com/tw0far/potiongames/commands/MyCommand.java`
2. Implement `ICommand` interface
3. Register in `CommandDispatcher.registerCommands()`
4. Update `HelpCommand.java` with help text
5. Test with `/pg my-command`

### Adding a Manager

1. Create interface in `managers/IMyManager.java`
2. Implement in `managers/MyManager.java`
3. Register in `PotionGames.java`
4. Add delegation methods in `PotionGames.java`
5. Use through `plugin.getMyManager()`

### Adding Configuration Options

1. Add to `src/main/resources/config.yml`
2. Add getter in `ConfigurationManager.java`
3. Document in `DOCUMENTATION.md`
4. Validate in `ConfigValidator.java` if critical

## Testing

### Manual Testing
1. Build: `mvn clean package`
2. Deploy JAR to test server
3. Test commands: `/pg help`
4. Check console for errors
5. Verify configuration loads

### Unit Tests
- Located in `src/test/java/`
- Run with: `mvn test`
- Add tests for new managers/commands

## Before Submitting

- [ ] Code compiles with 0 errors
- [ ] Code compiles with 0 warnings
- [ ] Tested on Paper 1.26+ server
- [ ] Updated CHANGELOG.md
- [ ] Updated documentation if needed
- [ ] Follows code style guidelines
- [ ] Comments on complex logic
- [ ] No debug code/println statements

## Pull Request Process

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Commit with clear messages: `git commit -m "Add: my feature"`
5. Push to your fork: `git push origin feature/my-feature`
6. Open a Pull Request with description

## Commit Message Format

```
<type>: <subject>

<body>

<footer>
```

### Types
- `Add:` New feature
- `Fix:` Bug fix
- `Refactor:` Code restructuring
- `Docs:` Documentation changes
- `Test:` Test changes
- `Build:` Build configuration

### Examples
```
Add: /pg broadcast command for server announcements
Fix: Setup mode chat input not being read
Refactor: Extract spell checking to ConfigValidator
Docs: Update BUILD.md with troubleshooting
```

## Questions?

- Check existing issues for solutions
- Review code comments
- Look at similar implementations
- Ask in pull request discussions

## Code of Conduct

Be respectful, helpful, and constructive in all interactions.

---

Thank you for contributing! 🎉
