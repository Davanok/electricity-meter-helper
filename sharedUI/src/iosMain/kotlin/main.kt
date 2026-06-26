import androidx.compose.ui.window.ComposeUIViewController
import com.davanok.electricitymeterhelper.App
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController
import platform.UIKit.setStatusBarStyle

fun MainViewController(): UIViewController =
    ComposeUIViewController { App(onThemeChanged = ::onThemeChanged) }

private fun onThemeChanged(isDark: Boolean) {
    UIApplication.sharedApplication.setStatusBarStyle(
        if (isDark) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
    )
}