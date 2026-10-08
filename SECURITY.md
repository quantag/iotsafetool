# Security policy

## Scope

This is a **host-side testing tool**. It holds no secrets of its own: keys are
generated and used on the card, and the tool only builds APDUs, sends them and
prints what comes back. It has not been through a security review.

Known limitations, including ones with security relevance, are published in
[docs/review-notes.md](docs/review-notes.md) rather than held back. Please read
that first — if your finding is already listed, an issue adding detail or a
patch is more useful than a private report.

Two things that are already known and are not news:

- **`-hmac` signs only the first 8 bytes of the SHA-256 digest it computes**
  (review note 1). A signature produced by this tool covers a 64-bit truncation
  of the file digest, and the tool reports success. Do not treat signatures
  produced by `-hmac` as covering the file.
- **The shaded jar bundles third-party libraries at old versions**
  (`bcprov-jdk15on` 1.70, `commons-lang3` 3.2.1). Vulnerabilities in those
  belong to their upstreams; see [docs/third-party.md](docs/third-party.md) for
  the versions to move to. Reports that a bundled library is outdated are
  welcome as ordinary issues, not as private reports.

## Reporting a vulnerability

For anything not already listed in `docs/review-notes.md`:

**Preferred:** use GitHub's private vulnerability reporting — the *Security* tab
of this repository, then *Report a vulnerability*.

**Alternative:** email <security@quantag-it.com>. If you want to encrypt the
report, say so in a first message without details and we will exchange keys.

Useful contents of a report:

- The command line that triggers it
- The APDU trace, which the tool prints for every exchange
- The card, applet and reader involved
- What an attacker gains, and what access they need to get there

## What to expect

| | |
| --- | --- |
| Acknowledgement | within 5 working days |
| Initial assessment | within 15 working days |
| Fix or published advisory | depends on severity |

We will say which of the two we are doing and why. Where something cannot be
fixed, we will document it in `docs/review-notes.md` and say so.

Reporters are credited in the advisory and in `CHANGELOG.md` unless they prefer
otherwise. There is no bug bounty.

## Supported versions

Only the current `main` branch receives fixes. There are no maintained release
branches.
