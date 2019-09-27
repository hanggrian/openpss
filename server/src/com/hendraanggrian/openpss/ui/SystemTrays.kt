package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.OpenPSSServer
import java.awt.MenuItem
import java.awt.MenuShortcut
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

inline fun systemTray(block: SystemTray.() -> Unit) = SystemTray.getSystemTray().block()

inline fun SystemTray.trayIcon(image: String, block: TrayIcon.() -> Unit) = add(
    TrayIcon(
        Toolkit
            .getDefaultToolkit()
            .getImage(OpenPSSServer::class.java.getResource(image))
    ).apply { block() }
)

inline fun TrayIcon.popupMenu(block: PopupMenu.() -> Unit) {
    popupMenu = PopupMenu().apply { block() }
}

fun PopupMenu.menuItem(
    text: String,
    shortcut: Int? = null,
    block: (MenuItem.() -> Unit)? = null
) {
    add(MenuItem(text)).also {
        if (shortcut != null) {
            it.shortcut = MenuShortcut(shortcut)
        }
        block?.invoke(it)
    }
}
