package com.fsck.k9.view

import com.fsck.k9.K9
import com.fsck.k9.ui.Theme
import com.fsck.k9.ui.ThemeManager

class WebViewConfigProvider(private val themeManager: ThemeManager) {
    fun createForMessageView() = createWebViewConfig(themeManager.messageViewTheme)

    fun createForMessageCompose() = createWebViewConfig(themeManager.messageComposeTheme)
    /**
     * 控制着收件箱内容区WebView的字体大小
     */
    private fun createWebViewConfig(theme: Theme): WebViewConfig {
        return WebViewConfig(
                useDarkMode = theme == Theme.DARK,
                autoFitWidth = K9.isAutoFitWidth,
                textZoom = K9.fontSizes.messageViewContentAsPercent
        )
    }
}
