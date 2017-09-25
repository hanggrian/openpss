package com.wijayaprinting.javafx.control

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.MenuItem

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class MenuItem2(text: String, onAction: ((ActionEvent) -> Unit)? = null) : MenuItem(text) {

    init {
        this.onAction = EventHandler { event -> onAction?.invoke(event) }
    }
}