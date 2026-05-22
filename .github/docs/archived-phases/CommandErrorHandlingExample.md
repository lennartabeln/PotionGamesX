# CommandErrorHandlingExample

Example integration of error handling in command execution. Shows how to handle different error scenarios with user-friendly messages.

> NOTE: Mock helper methods (isPlayerInGame, isLobbyFull, etc.) are intentionally empty or return constants to keep this example focused on error handling patterns. In production, these would contain actual game logic.

## Examples / Methods

- handleJoinCommand: Permission checks, in-game checks, lobby capacity and running checks, sending appropriate PotionGamesError messages to the player.
- handleSetupCommand: Argument validation, location type checks, and setup with error-handling and friendly messages for failures.
- handleStatsCommand: Querying player stats and handling database exceptions with both admin logging and friendly player messages.

## Notes
The original Java file contains usage examples of ErrorHandler methods: sendToPlayer, sendSuccess, sendInfo, handle, and logError. These show patterns for mapping internal errors to player-visible messages and admin logs.
