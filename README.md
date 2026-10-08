# IoT SAFE Tool

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](pom.xml)
[![Status: testing tool](https://img.shields.io/badge/status-testing%20tool-yellow.svg)](#project-status)

A command-line tool for exercising a **GSMA IoT SAFE** applet on a smart card
or SIM over PC/SC. It selects the applet, generates a key pair on the card,
reads back the public key, signs a digest and verifies the signature again —
logging every APDU in both directions so you can see exactly what the card was
asked and what it replied.

Developed by [Quantag IT Solutions GmbH](https://quantag-it.com) and released
under the Apache License 2.0.

---

## Which applet this talks to

> **This tool targets the standard GSMA IoT SAFE applet interface.** It does
> **not** drive the applet in [quantag/iotsafe](https://github.com/quantag/iotsafe),
> which implements a different, proprietary command set.
>
> The two are not interchangeable, and the failure is not clean: `0x52` and
> `0x54` exist in both interfaces with different meanings, so this tool's
> "store private key" lands on that applet's **DECRYPT**, and "store public
> key" on its **WRAP/UNWRAP**. You get a wrong operation rather than an error.

| | This tool expects | quantag/iotsafe applet |
| --- | --- | --- |
| Applet AID | `A00000003053F12401770101495341` | `A00000006203010C0101` |
| Generate key pair | `80 B9` | `80 40` |
| Store private key | `80 52` | `80 42` |
| Store public key | `80 54` | `80 44` |
| Compute signature | `80 2A` / `80 2B` | `80 50` |
| Get data / object list | `80 CB` | `80 30` |
| Get version | `80 70` | `80 70` |
| Get random | `80 84` | `80 84` |

Use this tool against a card whose applet implements the IoT SAFE interface as
published by GSMA — the APDU headers in
[`IoTSAFEDefines.java`](src/main/java/org/iotsafe/utils/IoTSAFEDefines.java)
are the authoritative list of what it sends.

## Building

Requires a JDK 8 or later and Apache Maven.

```bash
mvn clean package
```

This produces a self-contained `target/IoT_SAFE_Tool.jar` with dependencies
shaded in. Note that redistributing that jar means redistributing the bundled
third-party libraries — see [NOTICE](NOTICE) and
[docs/third-party.md](docs/third-party.md) for the obligations attached.

## Usage

```
java -jar IoT_SAFE_Tool.jar [options]
```

| Option | Effect |
| --- | --- |
| `-list` | List available card readers with their index |
| `-reader <index>` | Select the reader to use (default `0`) |
| `-version` | Read the applet version |
| `-genkeys` | Generate a key pair on the card, print the public key |
| `-getpub` | Read a public key back from the card |
| `-hmac <file>` | SHA-256 the file, sign the digest on the card, verify it |
| `-?`, `-h`, `-help` | Print the built-in help |

`-reader` must appear before the operation, and only the first operation found
is run.

### A typical session

```console
$ java -jar IoT_SAFE_Tool.jar -list
List of available card readers:
   0 : OMNIKEY Smart Card Reader USB 0

$ java -jar IoT_SAFE_Tool.jar -reader 0 -genkeys
NFC device found and connected
APDU-C: 00A404000FA00000003053F1240177010149534100
APDU-R: 9000
IoT SAFE applet selected
APDU-C: 80B900000484020002
APDU-R: 8402000285020002344549438641044AA6E6...9000
Generated Public Key: 8402000285020002344549438641044AA6E6...
```

Key pair `0002` is hard-coded as the object the tool operates on, as are the
signature session parameters. To work with other objects, edit the constants in
[`IoTSAFETool.java`](src/main/java/org/iotsafe/tool/IoTSAFETool.java) — making
them configurable is [a known gap](docs/review-notes.md).

## Repository layout

```
src/main/java/org/iotsafe/
  tool/IoTSAFEToolCL.java    Command-line entry point and option parsing
  tool/IoTSAFETool.java      The operations each option maps to
  utils/IoTSAFEDefines.java  APDU headers and the applet AID
  utils/IoTSAFETools.java    APDU construction and exchange with the card
  utils/Tools.java           Hex/byte conversion helpers
  exception/CardletException.java
  applet/InitializeApp.java  Standalone example: initialisation sequence
  applet/ReadDataApp.java    Standalone example: read application data
  applet/SignDataApp.java    Standalone example: sign and verify

docs/third-party.md          Bundled libraries and their license obligations
docs/review-notes.md         Known limitations found by source review
```

The three classes under `applet/` are standalone `main` methods kept from
development. They hard-code a reader name and are not reachable from the CLI.

## Project status

This is a **testing and development tool**, published as-is. It was written to
exercise an applet during development, not as a provisioning product, and it
shows: object identifiers and session parameters are hard-coded, there are no
automated tests, and errors are reported by printing a message rather than by a
non-zero exit status, so it does not script cleanly yet.

[docs/review-notes.md](docs/review-notes.md) lists what a user is most likely
to trip over, including the `-hmac` option signing only the first 8 bytes of
the digest it computes.

It is useful if you want to watch real APDU traffic against an IoT SAFE applet
and have a starting point you can modify. It is not a finished product, and
nothing here has been through a security review.

## Contributing

Bug reports and patches are welcome — see [CONTRIBUTING.md](CONTRIBUTING.md).
Contributions are accepted under Apache 2.0 with a Developer Certificate of
Origin sign-off.

For a security issue, follow [SECURITY.md](SECURITY.md) rather than opening a
public issue.

## Related

- [quantag/iotsafe](https://github.com/quantag/iotsafe) — Quantag's Java Card
  IoT SAFE applet. Different command set; see the warning above.

## Licence

Copyright 2023-2026 Quantag IT Solutions GmbH.

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for the
terms and [NOTICE](NOTICE) for the attribution requirement — if you
redistribute this software or a derivative of it, in source or binary form, you
must reproduce the contents of `NOTICE` in your accompanying documentation or
materials.

Commercial licensing, integration support and provisioning-stack work are
available from Quantag IT Solutions GmbH: <https://quantag-it.com>.
