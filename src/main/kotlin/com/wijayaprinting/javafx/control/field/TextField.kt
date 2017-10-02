package com.wijayaprinting.javafx.control.field

import javafx.scene.control.TextField

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class TextField() : TextField() {

    constructor(promptText: String) : this() {
        this.promptText = promptText
    }

    constructor(promptText: String, text: String) : this() {
        this.promptText = promptText
        this.text = text
    }
}