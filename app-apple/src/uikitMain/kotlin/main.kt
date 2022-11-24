import androidx.compose.ui.window.Application
import com.mikepenz.aboutlibraries.Libs
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIApplicationMain
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow
import platform.posix.memcpy

fun main() {
    val args = emptyArray<String>()
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
        }
    }
}


class SkikoAppDelegate @OverrideInit constructor() : UIResponder(), UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds).apply {
            rootViewController = Application("AboutLibraries") {
                SampleContent()
            }
            makeKeyAndVisible()
        }
        return true
    }
}

actual fun getJson(): Libs {
    val path = "aboutlibraries.json"
    val absolutePath = NSBundle.mainBundle.resourcePath + "/" + path
    val contentsAtPath: NSData? = NSFileManager.defaultManager().contentsAtPath(absolutePath)
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