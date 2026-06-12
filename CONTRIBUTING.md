# Contributing

Thanks for considering contributing to PotionGames!

## Reporting Bugs

Open a [bug report](https://github.com/andersspielen/PotionGames/issues/new?labels=bug&template=bug_report.yml) with your server software, plugin version, and steps to reproduce.

## Feature Requests

Open a [feature request](https://github.com/andersspielen/PotionGames/issues/new?labels=enhancement&template=feature_request.yml) describing the problem and your proposed solution.

## Security Issues

Do **not** open a public issue. See [`SECURITY.md`](SECURITY.md) for private disclosure.

## Pull Requests

1. Fork the repo and create your branch from `main`.
2. Run `mvn clean package` locally — ensure it builds without errors.
3. Keep changes focused; avoid unrelated formatting or noise.
4. Fill out the pull request template with what changed and how it was tested.
5. CI (build + CodeQL) must pass before merging.

## Build

```bash
mvn clean package
```

The JAR is written to `target/PotionGamesX-<version>.jar`.

## Code Style

- Java: 4-space indentation, no tabs
- YAML: 2-space indentation
- No trailing whitespace
- Files end with a single newline

An `.editorconfig` ships with the repo — most editors will pick it up automatically.
