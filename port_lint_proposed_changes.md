# port-lint Proposed Changes

**Generated:** 2026-08-24
**Source:** tmp/path-absolutize/src
**Target:** src/commonMain/kotlin

These are review proposals only. They are emitted when a Rust -> Kotlin pair matches only after fallback normalization, so the existing `port-lint` header is not an exact provenance match.

| Target file | Current header | Proposed header | Source path | Reason |
|-------------|----------------|-----------------|-------------|--------|
| `src/commonMain/kotlin/io/github/kotlinmania/pathabsolutize/PathAbsolutize.kt` | `// port-lint: source tmp/path-absolutize/src/lib.rs` | `// port-lint: source lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'tmp/path-absolutize/src/lib.rs' vs expected 'lib.rs'` |
| `src/commonTest/kotlin/io/github/kotlinmania/pathabsolutize/PathAbsolutizeTest.kt` | `// port-lint: tests tmp/path-absolutize/src/lib.rs` | `// port-lint: tests lib.rs` | `lib.rs` | `port-lint provenance header matched only after fallback normalization: 'tests:tmp/path-absolutize/src/lib.rs' vs expected 'lib.rs'` |
