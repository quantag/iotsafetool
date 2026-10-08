# Review notes

Limitations found by reading the source before publication. Line references are
against the current `main`. None of these is a vulnerability in a deployed
system — this is a development tool, and the list exists so that anyone picking
it up knows what they are getting.

Issue numbers are stable: a fixed issue keeps its number and is marked
**Fixed** rather than removed, so references from commits and other documents
stay meaningful.

| # | Severity | Status | Issue | Location |
| --- | --- | --- | --- | --- |
| 1 | High | **Fixed** | `-hmac` signs only the first 8 bytes of the digest it computes | `IoTSAFETool.java:152-176` |
| 2 | Medium | Open | `-hmac` with no filename throws `ArrayIndexOutOfBoundsException` | `IoTSAFEToolCL.java:74` |
| 3 | Medium | Open | Failures print a message and exit zero | `IoTSAFETool.java:76,98,142,196` |
| 4 | Medium | Open | Object identifiers and session parameters are hard-coded | `IoTSAFETool.java:58,148` |
| 5 | Low | Open | `-reader` with a bad index throws instead of reporting | `IoTSAFETool.java:54` |
| 6 | Low | Open | BouncyCastle is registered but never used | four files — see [third-party.md](third-party.md) |
| 7 | Low | Open | `applet/` classes hard-code a reader name and are unreachable | `InitializeApp`, `ReadDataApp`, `SignDataApp` |
| 8 | Info | Open | A variable is named `sdhkkshfjsk` | `IoTSAFETool.java:137` |

---

## 1. `-hmac` signs only the first 8 bytes of the digest — Fixed

**Was:** `IoTSAFEToolCL` computed a full SHA-256 digest of the file, 32 bytes.
`runHmac` then built the data-to-sign object with a hard-coded 2-byte prefix
`9B 08` — tag `9B`, length **8** — and copied exactly 8 bytes:

```java
byte[] addBytes = Tools.hexStringToBytes("9B08");
byte[] finalByteFile = new byte[addBytes.length + 8];       // byteFile.length
System.arraycopy(addBytes, 0, finalByteFile, 0, addBytes.length);
System.arraycopy(byteFile, 0, finalByteFile, addBytes.length, 8);  // byteFile.length
```

The commented-out `byteFile.length` on both lines showed the intent. As
written, 24 of the 32 digest bytes were discarded and the card signed a 64-bit
truncation, so two files agreeing in their first 8 digest bytes produced the
same signature. The signature then verified, because verification was handed
the same truncated object — the tool reported success and nothing looked wrong.

**Fixed by** letting the length follow the hash:

```java
byte[] finalByteFile = new byte[2 + byteFile.length];
finalByteFile[0] = IoTSAFEDefines.TAG_DATA_TO_SIGN;   // 0x9B
finalByteFile[1] = (byte) byteFile.length;            // 0x20 for SHA-256
System.arraycopy(byteFile, 0, finalByteFile, 2, byteFile.length);
```

`runHmac` now also rejects a null or empty hash, and rejects one longer than
`0x7F` bytes, which would need long-form TLV length encoding that this tool
does not implement. SHA-256, SHA-384 and SHA-512 digests all fit the short
form.

> **Not verified against a card.** The applet previously accepted an 8-byte
> object under tag `9B`; whether a given applet accepts a 32-byte one depends
> on the signature session it was opened with. If your card rejects the new
> object, the session parameters in `COMP_SIGN_INIT_OPEN_SESSION_0002` are
> where to look. Please report what you see.

The option remains misnamed: it computes a digest and asks the card for a
signature. No HMAC is involved.

## 2. `-hmac` with no filename throws

```java
String fileName = argsList[i+1];
```

There is no bounds check — the guard that would have provided one is commented
out immediately above. `-hmac` as the last argument exits with a raw
`ArrayIndexOutOfBoundsException` and a stack trace. The same pattern in the
`-reader` handler is guarded correctly, so this is an oversight rather than a
convention.

## 3. Failures print a message and exit zero

Every operation wraps its body in `catch (Exception e)` and prints
`"ERROR: " + e.getClass() + ": " + e.getMessage()`. The process then exits
normally. A caller cannot tell success from failure except by parsing stdout,
which makes the tool awkward to use from a script or a CI job.

**Fix direction:** exit non-zero on failure, and write errors to stderr.

## 4. Object identifiers and session parameters are hard-coded

Key pair `0002` (`84 02 00 02`) and the signature session parameters
(`84020002A1010191020001920104`) are `final` locals inside the methods that use
them. Working with any other object on the card means editing the source and
rebuilding.

**Fix direction:** accept the object identifier as a command-line argument.

## 5. `-reader` with a bad index throws instead of reporting

```java
this.readerName = terminals.get(Integer.parseInt(readerIndex)).getName();
```

A non-numeric value raises `NumberFormatException`; an out-of-range one raises
`IndexOutOfBoundsException`. Both happen in the constructor, before any
operation runs, and surface as a stack trace. An empty reader list produces
`IndexOutOfBoundsException` rather than the `CardletException("No card reader
available")` that the surrounding code clearly intends.

## 6. BouncyCastle is registered but never used

`Security.addProvider(new BouncyCastleProvider())` appears in four files and no
BC algorithm is ever requested. See
[third-party.md](third-party.md#bouncycastle-is-not-actually-used) — removing it
would cut about 8 MB from the shaded jar.

## 7. The `applet/` classes are unreachable

`InitializeApp`, `ReadDataApp` and `SignDataApp` each have a `main` method and
each hard-codes `"OMNIKEY Smart Card Reader USB 0"`. They are not reachable
from `IoTSAFEToolCL`, which is the jar's declared main class, and they will not
run on a machine with a different reader without an edit.

They are useful as worked examples — each carries a real APDU trace in a
trailing comment — which is why they are kept rather than deleted. The README
says what they are.

## 8. A variable is named `sdhkkshfjsk`

`IoTSAFETool.java:137`. It holds the public key object identifier
`85 02 00 02 00`. Renaming it is a one-line change and makes `runGetPublicKey`
readable.

---

## What is not here

There are no automated tests, and no CI job can exercise the card paths —
everything meaningful needs a physical reader and a card. The CI configuration
checks that the project compiles and that licensing stays intact; the rest is
manual.

## Reporting something not listed here

For a defect with security impact, follow [SECURITY.md](../SECURITY.md). For
everything else, an issue with the APDU trace and the card you used is the most
useful thing you can send.
