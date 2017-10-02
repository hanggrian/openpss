package com.wijayaprinting.javafx.control.field

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class PasswordField() : javafx.scene.control.PasswordField() {

    constructor(promptText: String) : this() {
        this.promptText = promptText
    }

    constructor(promptText: String, text: String) : this() {
        this.promptText = promptText
        this.text = text
    }
}