# Store descriptions

The README is written in Markdown, but not every platform accepts Markdown.

| Platform | Format | File to paste |
|---|---|---|
| **SpigotMC** | BBCode only (XenForo 1.5, Markdown is not supported) | [`spigot-description.txt`](spigot-description.txt) |
| **Modrinth** | Markdown | [`../../README.md`](../../README.md) |
| **Hangar** (PaperMC) | Markdown | [`../../README.md`](../../README.md) |
| **CurseForge** | Markdown | [`../../README.md`](../../README.md) |
| **BuiltByBit** | BBCode | [`spigot-description.txt`](spigot-description.txt) |

## SpigotMC

The resource description has no Markdown support - the staff answer every request for it with
"use a converter", because the board still runs XenForo 1.5 which only knows BBCode. Pasting
Markdown there leaves visible `#`, `**` and table pipes in the description.

`spigot-description.txt` is the same content as the README, translated to BBCode:

1. Edit the resource.
2. Switch the editor to BBCode / source view (the `<>` button) - pasting into the formatted view
   keeps the raw tags visible.
3. Paste the whole file, preview, save.

Headings use `[SIZE]`/`[B]`, colors are hex codes (`[COLOR=#55FF55]`), lists use `[LIST]`, and
inline file names and commands are wrapped in `[FONT=Courier New]` because Spigot has no inline
code tag.

## Keeping them in sync

When the README changes, update `spigot-description.txt` in the same pull request: the features,
command list and configuration files are the parts that drift first. The file has no version
number on purpose, so it does not need a bump when nothing user-visible changed.
