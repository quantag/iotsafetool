# Third-party components

The Maven build produces a shaded `jar-with-dependencies`, so the distributed
artifact embeds every declared dependency. **Shipping that jar is
redistributing these libraries**, and their license terms apply to you as the
distributor.

## Currently bundled

| Component | Version | License | Obligation when you redistribute |
| --- | --- | --- | --- |
| `org.bouncycastle:bcprov-jdk15on` | 1.70 | [Bouncy Castle License](https://www.bouncycastle.org/licence.html) (MIT-style) | Reproduce the copyright notice and permission notice |
| `org.apache.commons:commons-lang3` | 3.2.1 | Apache-2.0 | Reproduce its `LICENSE` **and** its own `NOTICE` file |

`NOTICE` in the repository root carries the attributions for both. If you build
your own distribution, make sure they travel with it.

## Removed

**`com.google.guava:guava` 18.0** — declared in `pom.xml` but never imported by
any source file. Removed, because a shaded jar embeds every declared dependency:
an unused one is pure attack surface and a license obligation for no benefit.
Guava 18.0 dates from 2014 and is affected by publicly documented
vulnerabilities in that line, including the `AtomicDoubleArray` /
`CompoundOrdering` deserialization issue and the insecure temporary-directory
behaviour. Nothing in this project used it.

## Recommended for an update

These versions are old enough that an update is worth doing before anyone
deploys the tool. The exact advisories and fixed versions move over time, so
**verify against a current advisory database rather than this table** — the
Dependabot configuration in `.github/dependabot.yml` will raise pull requests
automatically.

| Component | Current | Note |
| --- | --- | --- |
| `bcprov-jdk15on` | 1.70 (2021) | The `jdk15on` artifact is the legacy line; current releases ship as `bcprov-jdk18on`. Versions in the 1.7x range are affected by several published advisories. Moving to a current `jdk18on` release means changing the artifact ID, not only the version. |
| `commons-lang3` | 3.2.1 (2014) | Eleven years of fixes behind. Only `ArrayUtils.add` and `ArrayUtils.addAll` are used, in `IoTSAFETools`, so an update is low risk — or drop the dependency entirely and use `System.arraycopy`, which removes the last Apache-2.0 NOTICE obligation from the jar. |
| `maven.compiler.source/target` | 8 | Java 8 is past public free support. Nothing in the source needs a newer language level, but the target constrains contributors' toolchains. |

## BouncyCastle is not actually used

Worth knowing before anyone spends effort updating it. BouncyCastle appears in
four source files, and in every one the only use is:

```java
Security.addProvider(new BouncyCastleProvider());
```

No BC algorithm is ever requested. All cryptography happens on the card, and
the single JCA call in the tool — `MessageDigest.getInstance("SHA-256")` in
`IoTSAFEToolCL` — is satisfied by the JDK's own provider.

Removing the four `addProvider` calls and the dependency would cut roughly 8 MB
from the shaded jar, remove the largest third-party attack surface, and leave
only one bundled library. It is a behavioural change rather than a packaging
one, so it is left as a decision rather than made here; see
[review-notes.md](review-notes.md).

## Checking this yourself

```bash
mvn dependency:tree              # what actually ends up in the jar
mvn versions:display-dependency-updates
mvn org.owasp:dependency-check-maven:check   # advisory scan
```
