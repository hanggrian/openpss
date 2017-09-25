package com.wijayaprinting.javafx.control

import javafx.scene.control.PasswordField

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class PasswordField2() : PasswordField() {

    constructor(promptText: String) : this() {
        this.promptText = promptText
    }

    constructor(promptText: String, text: String) : this() {
        this.promptText = promptText
        this.text = text
    }
}