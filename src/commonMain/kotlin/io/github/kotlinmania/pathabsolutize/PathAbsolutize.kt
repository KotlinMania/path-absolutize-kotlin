// port-lint: source lib.rs
package io.github.kotlinmania.pathabsolutize

/**
 * Normalizes a path string to an absolute path by resolving dots (`.` and `..`)
 * without requiring filesystem access or file existence.
 */
object PathAbsolutizer {
    /**
     * Resolves a path relative to the given base/current working directory [cwd].
     */
    fun absolutizeFrom(path: String, cwd: String): String {
        val normalizedPath = path.replace('\\', '/')
        val normalizedCwd = cwd.replace('\\', '/')

        val isAbsolute = normalizedPath.startsWith('/')
        val combined = if (isAbsolute) {
            normalizedPath
        } else {
            val base = if (normalizedCwd.endsWith('/') && normalizedCwd.length > 1) {
                normalizedCwd.dropLast(1)
            } else if (normalizedCwd == "/") {
                ""
            } else {
                normalizedCwd
            }
            "$base/$normalizedPath"
        }

        val segments = mutableListOf<String>()
        val hasLeadingSlash = combined.startsWith('/')

        for (part in combined.split('/')) {
            when (part) {
                "", "." -> continue
                ".." -> {
                    if (segments.isNotEmpty() && segments.last() != "..") {
                        segments.removeAt(segments.size - 1)
                    } else if (!hasLeadingSlash) {
                        segments.add("..")
                    }
                }
                else -> segments.add(part)
            }
        }

        val result = if (hasLeadingSlash) {
            "/" + segments.joinToString("/")
        } else {
            if (segments.isEmpty()) "." else segments.joinToString("/")
        }
        return result
    }

    /**
     * Resolves a path within a virtual root directory. Any attempt to navigate
     * above the virtual root via `..` is clamped to the virtual root.
     */
    fun absolutizeVirtually(path: String, virtualRoot: String): String {
        val normalizedRoot = if (virtualRoot.endsWith('/') && virtualRoot.length > 1) {
            virtualRoot.dropLast(1)
        } else {
            virtualRoot
        }
        val resolved = absolutizeFrom(path, normalizedRoot)
        return if (resolved.startsWith(normalizedRoot)) {
            resolved
        } else {
            normalizedRoot
        }
    }
}

/**
 * Extension function to absolutize a path string relative to a base directory.
 */
fun String.absolutizeFrom(cwd: String): String = PathAbsolutizer.absolutizeFrom(this, cwd)

/**
 * Extension function to absolutize a path string within a virtual root.
 */
fun String.absolutizeVirtually(virtualRoot: String): String = PathAbsolutizer.absolutizeVirtually(this, virtualRoot)
