// port-lint: source lib.rs
package io.github.kotlinmania.pathabsolutize

/**
 * Trait/interface for path absolutization operations.
 */
interface Absolutize {
    fun absolutize(): String

    fun absolutizeFrom(cwd: String): String

    fun absolutizeVirtually(virtualRoot: String): String
}

/**
 * Normalizes a path string to an absolute path by resolving dots (`.` and `..`)
 * without requiring filesystem access or file existence.
 */
object PathAbsolutizer {
    /**
     * Resolves a path to an absolute path using "/" as root default if no CWD provided.
     */
    fun absolutize(path: String): String = absolutizeFrom(path, "/")

    /**
     * Resolves a path relative to the given base/current working directory [cwd].
     */
    fun absolutizeFrom(path: String, cwd: String): String {
        val normalizedPath = path.replace('\\', '/')
        val normalizedCwd = cwd.replace('\\', '/')

        val (prefix, cleanPath) = extractWindowsPrefix(normalizedPath)
        val (cwdPrefix, cleanCwd) = extractWindowsPrefix(normalizedCwd)

        val effectivePrefix = if (prefix.isNotEmpty()) prefix else cwdPrefix
        val isAbsolute = cleanPath.startsWith('/') || prefix.isNotEmpty()

        val combined =
            if (isAbsolute) {
                cleanPath
            } else {
                val base =
                    if (cleanCwd.endsWith('/') && cleanCwd.length > 1) {
                        cleanCwd.dropLast(1)
                    } else if (cleanCwd == "/") {
                        ""
                    } else {
                        cleanCwd
                    }
                "$base/$cleanPath"
            }

        val segments = mutableListOf<String>()
        val hasLeadingSlash = combined.startsWith('/') || effectivePrefix.isNotEmpty()

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

        val joined =
            if (hasLeadingSlash) {
                "/" + segments.joinToString("/")
            } else {
                if (segments.isEmpty()) "." else segments.joinToString("/")
            }

        return if (effectivePrefix.isNotEmpty()) {
            effectivePrefix + joined
        } else {
            joined
        }
    }

    /**
     * Resolves a path within a virtual root directory. Any attempt to navigate
     * above the virtual root via `..` is clamped to the virtual root.
     */
    fun absolutizeVirtually(path: String, virtualRoot: String): String {
        val normalizedRoot =
            if (virtualRoot.endsWith('/') && virtualRoot.length > 1) {
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

    private fun extractWindowsPrefix(path: String): Pair<String, String> {
        if (path.length >= 2 && path[1] == ':' && ((path[0] in 'a'..'z') || (path[0] in 'A'..'Z'))) {
            val prefix = path.substring(0, 2)
            val rest = path.substring(2)
            return Pair(prefix, rest)
        }
        return Pair("", path)
    }
}

/**
 * Extension function to absolutize a path string.
 */
fun String.absolutize(): String = PathAbsolutizer.absolutize(this)

/**
 * Extension function to absolutize a path string relative to a base directory.
 */
fun String.absolutizeFrom(cwd: String): String = PathAbsolutizer.absolutizeFrom(this, cwd)

/**
 * Extension function to absolutize a path string within a virtual root.
 */
fun String.absolutizeVirtually(virtualRoot: String): String = PathAbsolutizer.absolutizeVirtually(this, virtualRoot)
