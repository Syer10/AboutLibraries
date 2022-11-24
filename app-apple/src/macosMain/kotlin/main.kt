import androidx.compose.ui.window.Window
import com.mikepenz.aboutlibraries.Libs
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AppKit.NSApp
import platform.AppKit.NSApplication
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.posix.memcpy

fun main() {
    NSApplication.sharedApplication()
    Window("AboutLibraries") {
        SampleContent()
    }
    NSApp?.run()
}

actual fun getJson(): Libs {
    val path = "aboutlibraries.json"
    val currentDirectoryPath = NSFileManager.defaultManager().currentDirectoryPath
    val contentsAtPath: NSData? = NSFileManager.defaultManager().run {
        //todo in future bundle resources with app and use all sourceSets (skikoMain, nativeMain)
        contentsAtPath("$currentDirectoryPath/src/macosMain/resources/$path")
            ?: contentsAtPath("$currentDirectoryPath/src/commonMain/resources/$path")
    }
    if (contentsAtPath != null) {
        val byteArray = ByteArray(contentsAtPath.length.toInt())
        byteArray.usePinned {
            memcpy(it.addressOf(0), contentsAtPath.bytes, contentsAtPath.length)
        }
        return Libs.Builder().withJson(byteArray.decodeToString()).build()
    } else {
        throw MissingResourceException(path)
    }
}