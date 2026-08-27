// port-lint: tests lib.rs
package io.github.kotlinmania.pathabsolutize

import kotlin.test.Test
import kotlin.test.assertEquals

class PathAbsolutizeTest {
    @Test
    fun testAbsolutizeDefault() {
        val path = "path/to/./123/../456"
        assertEquals("/path/to/456", path.absolutize())
    }

    @Test
    fun testAbsolutizeAlreadyAbsolute() {
        val path = "/path/to/123/456"
        assertEquals("/path/to/123/456", path.absolutizeFrom("/current/dir"))
    }

    @Test
    fun testAbsolutizeWithDots() {
        val path = "/path/to/./123/../456"
        assertEquals("/path/to/456", path.absolutizeFrom("/current/dir"))
    }

    @Test
    fun testAbsolutizeRelativePath() {
        val path = "./path/to/123/456"
        assertEquals("/current/dir/path/to/123/456", path.absolutizeFrom("/current/dir"))
    }

    @Test
    fun testAbsolutizeParentDots() {
        val path = "../path/to/123/456"
        assertEquals("/current/path/to/123/456", path.absolutizeFrom("/current/dir"))
    }

    @Test
    fun testAbsolutizeRootParentClamping() {
        val path = "../../path/to/123/456"
        assertEquals("/path/to/123/456", path.absolutizeFrom("/current"))
    }

    @Test
    fun testAbsolutizeVirtually() {
        val path = "sub/dir/file.txt"
        assertEquals("/var/www/sub/dir/file.txt", path.absolutizeVirtually("/var/www"))

        val escapeAttempt = "../../etc/passwd"
        assertEquals("/var/www", escapeAttempt.absolutizeVirtually("/var/www"))
    }

    @Test
    fun testWindowsPrefix() {
        val path = "C:\\path\\to\\.\\123\\..\\456"
        assertEquals("C:/path/to/456", path.absolutize())

        val relPath = "foo\\bar"
        assertEquals("D:/work/foo/bar", relPath.absolutizeFrom("D:\\work"))
    }
}
