# ConfigurationIntegrationExample

Complete example showing how to use the optimized configuration and database systems together.

This document summarizes the examples in the original Java source:

- Loading configuration with translation keys and per-lobby customization
- Building chest loot and shop items with the project's DSL
- Querying and updating a database using prepared statements and a DatabaseQueryBuilder
- Performance and configuration reload examples

## Examples included

1. Initialize plugin configuration on startup: load global and per-lobby settings, log results.
2. Create arena loot based on lobby settings (ChestLootBuilder usage examples).
3. Create lobby-specific shop with ShopBuilder and conditional items based on settings.
4. Query player statistics with optimized database access and prepared statements.
5. Update player stats (record kills/deaths) with safe prepared statements and insert-if-missing logic.
6. Display top players from a lobby (ordering/limits).
7. Configuration update and reload examples.
8. Performance metrics / cache stats logging.

## Notes
The original Java file contains concrete example code for each case to illustrate usage of YamlConfigLoader, ChestLootBuilder, ShopBuilder, and DatabaseQueryBuilder. The snippets are useful as a reference for plugin authors wanting to use the configuration and DB utilities.
