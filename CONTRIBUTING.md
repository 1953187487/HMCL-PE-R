# Contributing to HMCL-PE-Renewed

Thank you for considering a contribution. This is a community fork of
[Tungstend's HMCL-PE](https://github.com/Tungstend/HMCL-PE). By submitting
a change, you accept the following terms.

## Legal

- The project is licensed under **GPLv3**. See `LICENSE` and
  `DERIVED-FROM.md`.
- All contributions are released under GPLv3. Do not add proprietary
  code, proprietary libraries, or content under a non-compatible license.
- Do not strip or modify the upstream copyright headers in Java files.
- Do not remove the `DERIVED-FROM.md` attribution.

## Build

HMCL-PE-R requires Android Studio Hedgehog or newer with:

- Android SDK 34
- NDK (r26 or later)
- CMake 3.22.1

Build:

```
./gradlew assembleDebug
```

Release signing requires a keystore `HMCLPE/release.jks` and the
following environment variables:

```
export HMCLR_KEYSTORE_PASS=...
export HMCLR_KEY_ALIAS=...
export HMCLR_KEY_PASS=...
```

## Branches

- `main`: latest development line.
- `release/v1.0.x`: patch releases for the 1.0 series.
- `upstream-sync/YYYY-MM`: sync branches for pulling in updates from
  upstream HMCL or HMCL-PE releases (if any).

## Code style

- Preserve the existing Java 8 style in the `com.tungsten.hmclpe`
  package unless a whole module is migrated together.
- Kotlin-only for **new** files under `com.hmclpe.renewed.*`.
- Follow Android coding conventions:
  - Lowercase package names, PascalCase classes, camelCase methods.
  - Public methods take `@NonNull`/`@Nullable` where nullability matters.

## Reporting bugs

- Open a GitHub issue on this repository.
- Attach a logcat capture from the crash.
- Describe device, Android version, Minecraft version, and mods installed.

## Reporting security issues

Do **not** open a public issue. Contact the maintainer via a private
GitHub advisory.

## Trademark and branding

The `HMCL` and `HMCL-PE` names are attributed to their original authors
in `DERIVED-FROM.md`. Do not use them to imply endorsement. Use
`HMCL-PE-R` or `HMCL-PE-Renewed` for this fork in user-facing text.
