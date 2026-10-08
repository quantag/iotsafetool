<!--
Copyright 2023-2026 Quantag IT Solutions GmbH
SPDX-License-Identifier: Apache-2.0
-->

## What this changes

<!-- One or two sentences. Link the issue or review note if there is one. -->

## Tested on

<!--
Which card and reader. "Not tested against hardware" is a fine answer and
useful to know — silence is not.
-->

- Card and applet:
- Reader:
- Java version:

## APDU trace

<!-- Before and after, for anything that changes what goes to the card. -->

```text

```

## Checklist

- [ ] Commits are signed off (`git commit -s`) — see [CONTRIBUTING.md](https://github.com/quantag/iotsafetool/blob/main/CONTRIBUTING.md)
- [ ] `mvn clean package` passes
- [ ] New files carry the licence header, existing copyright lines untouched
- [ ] `docs/review-notes.md` updated if this fixes or affects a listed item
- [ ] `CHANGELOG.md` updated under `[Unreleased]`
- [ ] No new dependency — or, if there is one, `docs/third-party.md` and `NOTICE` updated with its licence obligation
- [ ] APDU changes cite the GSMA specification section they follow
