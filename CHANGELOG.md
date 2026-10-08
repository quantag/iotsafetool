# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Security

- **`-hmac` now signs the whole digest** (review note 1). It computed a 32-byte
  SHA-256 digest of the file, then built the data-to-sign object with a
  hard-coded `9B 08` — tag `9B`, length 8 — and copied only 8 bytes, so the
  card signed a 64-bit truncation. Verification was handed the same truncated
  object, so the tool printed "Signature verification successfully completed"
  and nothing looked wrong. Two files agreeing in their first 8 digest bytes
  produced identical signatures.

  The length now follows the hash. `runHmac` also rejects a null or empty
  hash, and rejects one longer than `0x7F` bytes, which would need long-form
  TLV length encoding that this tool does not implement — SHA-256, SHA-384 and
  SHA-512 all fit the short form.

  **Any signature produced by an earlier build covers 8 bytes of the digest,
  not the file. Regenerate them.**

  Not yet verified against a card: whether a given applet accepts a 32-byte
  data-to-sign object depends on the signature session it was opened with.

### Added

- Apache License 2.0, with a `NOTICE` file carrying the attribution requirement
  under section 4(d), and SPDX headers on all nine Java sources.
- `README.md` documenting what the tool does, every command-line option, the
  build, and — prominently — that it targets the standard GSMA IoT SAFE
  interface and does **not** drive the applet in `quantag/iotsafe`.
- `docs/third-party.md` — the libraries the shaded jar embeds, the licence
  obligations redistribution creates, and the versions to move to.
- `docs/review-notes.md` — eight limitations found by source review, with line
  references. Note 1, since fixed, was the one that mattered: `-hmac` signed
  only the first 8 bytes of the SHA-256 digest it computes.
- `CONTRIBUTING.md` and `SECURITY.md`.
- Continuous integration: Maven build, licence header check, markdown link
  check.
- Dependabot configuration for Maven and GitHub Actions, so the dependency
  situation does not drift again.

### Changed

- Copyright attributed to Quantag IT Solutions GmbH across all source files,
  with class-level documentation added where there was none.
- `.gitignore` extended for Maven, IDE and OS output.

### Removed

- **`com.google.guava:guava` 18.0** from `pom.xml` — declared but never
  imported by any source file. Because the build shades every dependency into
  the distributed jar, an unused one was attack surface and a licence
  obligation for no benefit.
- `.idea/` IntelliJ configuration from version control. It also referenced
  BouncyCastle 1.54 while `pom.xml` declared 1.70.

### Known at time of publication

No functional change to the tool: it behaves exactly as the version that was in
internal use. The items in `docs/review-notes.md` are documented, not fixed.

## [1.0] — 2023-10-20

Initial internal version, and the code as first published.

### Added

- Command-line tool over `javax.smartcardio` for exercising a GSMA IoT SAFE
  applet: reader enumeration, applet selection, applet version query, on-card
  key pair generation, public key retrieval, and a sign-then-verify round trip,
  logging every APDU in both directions.
- `IoTSAFETools` — APDU construction and exchange for the IoT SAFE interface,
  covering key and file storage, get-data for the application and object list,
  and the compute/verify signature session commands.
- Standalone examples under `applet/` for the initialisation, read-data and
  sign-data sequences, each carrying a recorded APDU trace.
- Maven build producing a self-contained `IoT_SAFE_Tool.jar`.

[Unreleased]: https://github.com/quantag/iotsafetool/compare/v1.0...HEAD
[1.0]: https://github.com/quantag/iotsafetool/releases/tag/v1.0
