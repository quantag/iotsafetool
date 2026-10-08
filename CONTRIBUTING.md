# Contributing

This is a development tool published as-is, and it has rough edges that are
written down in [docs/review-notes.md](docs/review-notes.md) rather than hidden.
Patches that file those edges off are welcome.

## Licence and sign-off

This project is licensed under the Apache License 2.0. Contributions are
accepted under the same licence, with a Developer Certificate of Origin
sign-off — no separate CLA.

Add a `Signed-off-by` line to each commit, which `git commit -s` does for you:

```
Signed-off-by: Your Name <your.email@example.com>
```

By signing off you certify that you wrote the contribution or otherwise have the
right to submit it under Apache 2.0, as set out in the
[Developer Certificate of Origin](https://developercertificate.org/).

New files need the standard header; copy it from any existing source file. The
copyright line stays as it is — please do not add your own, and do not remove
the `SPDX-License-Identifier` line.

## Before you open a pull request

- **`mvn clean package` passes.** There are no tests to run, so compiling and a
  manual check against a card is the whole gate.
- **Say which card and reader you tested on**, or say that you could not test
  against hardware. Either is fine to know; silence is not.
- **Include the APDU trace** for anything that changes what goes to the card.
  The tool prints one for every exchange, so this is a copy-paste.
- **Adding a dependency needs a reason.** The build shades everything into one
  jar, so each dependency becomes something downstream users must comply with
  and keep patched — see [docs/third-party.md](docs/third-party.md). If the JDK
  can do it, prefer the JDK.
- **Update `docs/review-notes.md`** if your change fixes or affects a listed
  item, and `CHANGELOG.md` under `[Unreleased]`.

## Good first changes

From [docs/review-notes.md](docs/review-notes.md), in rough order of value:

1. **Issue 1 is fixed**, but nobody has run it against a card yet. If you have
   one, check that your applet accepts a 32-byte data-to-sign object and say so
   in an issue — that is the most useful thing anyone can contribute right now.
2. **Issue 3** — exit non-zero on failure and write errors to stderr, so the
   tool can be scripted.
3. **Issue 4** — take the object identifier as an argument instead of
   hard-coding key pair `0002`.
4. **Issue 2** — bounds-check the `-hmac` filename argument.

## Which applet this targets

The tool speaks the **standard GSMA IoT SAFE interface**, not the command set of
[quantag/iotsafe](https://github.com/quantag/iotsafe). Patches that change APDU
headers should reference the GSMA specification section they follow, so the next
reader can check them. Please do not add commands from the proprietary interface
here: keeping the two apart is deliberate, and the README explains why.

## Style

Existing code uses four-space indentation and mixes it with tabs in
`IoTSAFETools.java` and `IoTSAFEDefines.java`. Match whatever the file you are
editing already does rather than reformatting it — a whitespace-only diff across
a file makes the real change invisible.

## Reporting bugs

Open an issue with the command you ran, the APDU trace, and the card and reader
you used. For anything with security impact, follow [SECURITY.md](SECURITY.md)
instead of filing publicly.
