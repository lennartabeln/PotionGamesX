# Contributing

Thanks for considering contributing to PotionGamesX!

## Reporting Bugs

Open a [bug report](https://github.com/lennartabeln/PotionGamesX/issues/new?labels=bug&template=bug_report.md) with your server software, plugin version, and steps to reproduce.

## Feature Requests

Open a [feature request](https://github.com/lennartabeln/PotionGamesX/issues/new?labels=enhancement&template=feature_request.md) describing the problem and your proposed solution.

## Security Issues

Do **not** open a public issue. See [`SECURITY.md`](SECURITY.md) for private disclosure.

## Pull Requests

1. Fork the repo and create your branch from `main`.
2. Run `mvn clean install` locally — the build must pass without errors **and** without warnings (warnings are errors).
3. Keep changes focused; avoid unrelated formatting or noise.
4. Fill out the pull request template with what changed and how it was tested.
5. CI (build + CodeQL) must pass before merging.

## Build

```bash
mvn clean install
```

The JAR is written to `target/PotionGamesX-<version>.jar`. It is a Paper plugin: copy it into a server's `plugins/` folder, it cannot be started with `java -jar`.

`maven-compiler-plugin` enables `-Xlint:all` and `failOnWarning`, so any javac warning (deprecation, unchecked cast, raw type, …) fails the build. Fix the warning instead of suppressing it — the few justified `@SuppressWarnings("unchecked")` cases carry a comment explaining why.

## Code Style

- Java: 4-space indentation, no tabs
- YAML: 2-space indentation
- No trailing whitespace
- Files end with a single newline

An `.editorconfig` ships with the repo — most editors will pick it up automatically.
