# path-absolutize-kotlin in Kotlin

[![GitHub link](https://img.shields.io/badge/GitHub-KotlinMania%2Fpath--absolutize--kotlin-blue.svg)](https://github.com/KotlinMania/path-absolutize-kotlin)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/path-absolutize-kotlin)](https://central.sonatype.com/artifact/io.github.kotlinmania/path-absolutize-kotlin)
[![Build status](https://img.shields.io/github/actions/workflow/status/KotlinMania/path-absolutize-kotlin/ci.yml?branch=main)](https://github.com/KotlinMania/path-absolutize-kotlin/actions)

This is a Kotlin Multiplatform line-by-line transliteration port of [`magiclen/path-absolutize`](https://github.com/magiclen/path-absolutize).

**Original Project:** This port is based on [`magiclen/path-absolutize`](https://github.com/magiclen/path-absolutize). All design credit and project intent belong to the upstream authors; this repository is a faithful port to Kotlin Multiplatform with no behavioural changes intended.

### Porting status

This is an **in-progress port**. The goal is feature parity with the upstream Rust crate while providing a native Kotlin Multiplatform API. Every Kotlin file carries a `// port-lint: source <path>` header naming its upstream Rust counterpart so the AST-distance tool can track provenance.

---

## Upstream README — `magiclen/path-absolutize`

> The text below is reproduced and lightly edited from [`https://github.com/magiclen/path-absolutize`](https://github.com/magiclen/path-absolutize). It is the upstream project's own description and remains under the upstream authors' authorship; links have been rewritten to absolute upstream URLs so they continue to resolve from this repository.

## Path Absolutize


[![CI](https://github.com/magiclen/path-absolutize/actions/workflows/ci.yml/badge.svg)](https://github.com/magiclen/path-absolutize/actions/workflows/ci.yml)

This is a library for extending `Path` and `PathBuf` in order to get an absolute path and remove the containing dots.

The difference between `absolutize` and `canonicalize` methods is that `absolutize` does not care about whether the file exists and what the file really is.

Please read the following examples to know the parsing rules.

## Examples

There are two methods you can use.

### absolutize

Get an absolute path.

The dots in a path will be parsed even if it is already an absolute path (which means the path starts with a `MAIN_SEPARATOR` on Unix-like systems).

```rust
use std::path::Path;

use path_absolutize::*;

let p = Path::new("/path/to/123/456");

assert_eq!("/path/to/123/456", p.absolutize().unwrap().to_str().unwrap());
```

```rust
use std::path::Path;

use path_absolutize::*;

let p = Path::new("/path/to/./123/../456");

assert_eq!("/path/to/456", p.absolutize().unwrap().to_str().unwrap());
```

If a path starts with a single dot, the dot means your program's **current working directory** (CWD).

```rust
use std::path::Path;
use std::env;

use path_absolutize::*;

let p = Path::new("./path/to/123/456");

assert_eq!(Path::join(env::current_dir().unwrap().as_path(), Path::new("path/to/123/456")).to_str().unwrap(), p.absolutize().unwrap().to_str().unwrap());
```

If a path starts with a pair of dots, the dots means the parent of the CWD. If the CWD is **root**, the parent is still **root**.

```rust
use std::path::Path;
use std::env;

use path_absolutize::*;

let p = Path::new("../path/to/123/456");

let cwd = env::current_dir().unwrap();

let cwd_parent = cwd.parent();

match cwd_parent {
   Some(cwd_parent) => {
       assert_eq!(Path::join(&cwd_parent, Path::new("path/to/123/456")).to_str().unwrap(), p.absolutize().unwrap().to_str().unwrap());
   }
   None => {
       assert_eq!(Path::join(Path::new("/"), Path::new("path/to/123/456")).to_str().unwrap(), p.absolutize().unwrap().to_str().unwrap());
   }
}
```

A path which does not start with a `MAIN_SEPARATOR`, **Single Dot** and **Double Dots**, will act like having a single dot at the start when `absolutize` method is used.

```rust
use std::path::Path;
use std::env;

use path_absolutize::*;

let p = Path::new("path/to/123/456");

assert_eq!(Path::join(env::current_dir().unwrap().as_path(), Path::new("path/to/123/456")).to_str().unwrap(), p.absolutize().unwrap().to_str().unwrap());
```

```rust
use std::path::Path;
use std::env;

use path_absolutize::*;

let p = Path::new("path/../../to/123/456");

let cwd = env::current_dir().unwrap();

let cwd_parent = cwd.parent();

match cwd_parent {
   Some(cwd_parent) => {
       assert_eq!(Path::join(&cwd_parent, Path::new("to/123/456")).to_str().unwrap(), p.absolutize().unwrap().to_str().unwrap());
   }
   None => {
       assert_eq!(Path::join(Path::new("/"), Path::new("to/123/456")).to_str().unwrap(), p.absolutize().unwrap().to_str().unwrap());
   }
}
```

### Starting from a given current working directory

With the `absolutize_from` function, you can provide the current working directory that the relative paths should be resolved from.

```rust
use std::env;
use std::path::Path;

use path_absolutize::*;

let p = Path::new("../path/to/123/456");
let cwd = env::current_dir().unwrap();

println!("{}", p.absolutize_from(cwd).unwrap().to_str().unwrap());
```


### absolutize_virtually

Get an absolute path **only under a specific directory**.

The dots in a path will be parsed even if it is already an absolute path (which means the path starts with a `MAIN_SEPARATOR` on Unix-like systems).

```rust
use std::path::Path;

use path_absolutize::*;

let p = Path::new("/path/to/123/456");

assert_eq!("/path/to/123/456", p.absolutize_virtually("/").unwrap().to_str().unwrap());
```

```rust
use std::path::Path;

use path_absolutize::*;

let p = Path::new("/path/to/./123/../456");

assert_eq!("/path/to/456", p.absolutize_virtually("/").unwrap().to_str().unwrap());
```

Every absolute path should under the virtual root.

```rust
use std::path::Path;

use std::io::ErrorKind;

use path_absolutize::*;

let p = Path::new("/path/to/123/456");

assert_eq!(ErrorKind::InvalidInput, p.absolutize_virtually("/virtual/root").unwrap_err().kind());
```

Every relative path should under the virtual root.

```rust
use std::path::Path;

use std::io::ErrorKind;

use path_absolutize::*;

let p = Path::new("./path/to/123/456");

assert_eq!(ErrorKind::InvalidInput, p.absolutize_virtually("/virtual/root").unwrap_err().kind());
```

```rust
use std::path::Path;

use std::io::ErrorKind;

use path_absolutize::*;

let p = Path::new("../path/to/123/456");

assert_eq!(ErrorKind::InvalidInput, p.absolutize_virtually("/virtual/root").unwrap_err().kind());
```

A path which does not start with a `MAIN_SEPARATOR`, **Single Dot** and **Double Dots**, will be located in the virtual root after the `absolutize_virtually` method is used.

```rust
use std::path::Path;

use path_absolutize::*;

let p = Path::new("path/to/123/456");

assert_eq!("/virtual/root/path/to/123/456", p.absolutize_virtually("/virtual/root").unwrap().to_str().unwrap());
```

```rust
use std::path::Path;

use path_absolutize::*;

let p = Path::new("path/to/../../../../123/456");

assert_eq!("/virtual/root/123/456", p.absolutize_virtually("/virtual/root").unwrap().to_str().unwrap());
```

## Caching

By default, the `absolutize` method and the `absolutize_virtually` method create a new `PathBuf` instance of the CWD every time in their operation. Although it allows us to safely change the CWD at runtime by the program itself (e.g. using the `std::env::set_current_dir` function) or outside controls (e.g. using gdb to call `chdir`), we don't need that in most cases.

In order to parse paths with better performance, this crate provides three ways to cache the CWD.

### once_cell_cache

Enabling the `once_cell_cache` feature can let this crate use `once_cell` to cache the CWD. It's thread-safe and does not need to modify any code, but once the CWD is cached, it cannot be changed anymore at runtime.

```toml
[dependencies.path-absolutize]
version = "*"
features = ["once_cell_cache"]
```

### lazy_static_cache

Enabling the `lazy_static_cache` feature can let this crate use `lazy_static` to cache the CWD. It's thread-safe and does not need to modify any code, but once the CWD is cached, it cannot be changed anymore at runtime.

```toml
[dependencies.path-absolutize]
version = "*"
features = ["lazy_static_cache"]
```

### unsafe_cache

Enabling the `unsafe_cache` feature can let this crate use a mutable static variable to cache the CWD. It allows the program to change the CWD at runtime by the program itself, but it's not thread-safe.

You need to use the `update_cwd` function to initialize the CWD first. The function should also be used to update the CWD after the CWD is changed.

```toml
[dependencies.path-absolutize]
version = "*"
features = ["unsafe_cache"]
```

```rust
use std::path::Path;

use path_absolutize::*;

unsafe {
    update_cwd();
}

let p = Path::new("./path/to/123/456");

println!("{}", p.absolutize().unwrap().to_str().unwrap());

std::env::set_current_dir("/").unwrap();

unsafe {
    update_cwd();
}

println!("{}", p.absolutize().unwrap().to_str().unwrap());
```

## Benchmark

#### No-cache

```bash
cargo bench
```

#### once_cell_cache

```bash
cargo bench --features once_cell_cache
```

#### lazy_static_cache

```bash
cargo bench --features lazy_static_cache
```

#### unsafe_cache

```bash
cargo bench --features unsafe_cache
```

## Crates.io

https://crates.io/crates/path-absolutize

## Documentation

https://docs.rs/path-absolutize

## License

[MIT](https://github.com/magiclen/path-absolutize/blob/HEAD/LICENSE)

---

## About this Kotlin port

### Installation

```kotlin
dependencies {
    implementation("io.github.kotlinmania:path-absolutize-kotlin:0.1.0-SNAPSHOT")
}
```

### Building

```bash
./gradlew build
./gradlew test
```

### Targets

- macOS arm64
- Linux x64
- Windows mingw-x64
- iOS arm64 / simulator-arm64 (Swift export + XCFramework)
- JS (browser + Node.js)
- Wasm-JS (browser + Node.js)
- Android (API 24+)

### Porting guidelines

See [AGENTS.md](AGENTS.md) and [CLAUDE.md](CLAUDE.md) for translator discipline, port-lint header convention, and Rust → Kotlin idiom mapping.

### License

This Kotlin port is distributed under the same MIT license as the upstream [`magiclen/path-absolutize`](https://github.com/magiclen/path-absolutize). See [LICENSE](LICENSE) (and any sibling `LICENSE-*` / `NOTICE` files mirrored from upstream) for the full text.

Original work copyrighted by the path-absolutize authors.  
Kotlin port: Copyright (c) 2026 Sydney Renee and The Solace Project.

### Acknowledgments

Thanks to the [`magiclen/path-absolutize`](https://github.com/magiclen/path-absolutize) maintainers and contributors for the original Rust implementation. This port reproduces their work in Kotlin Multiplatform; bug reports about upstream design or behavior should go to the upstream repository.
