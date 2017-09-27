package com.wijayaprinting.javafx.control

import javafx.scene.control.TextField

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class PromptTextField() : TextField() {

    constructor(promptText: String) : this() {
        this.promptText = promptText
    }

    constructor(promptText: String, text: String) : this() {
        this.promptText = promptText
        this.text = text
    }
}