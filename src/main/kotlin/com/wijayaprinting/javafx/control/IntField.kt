package com.wijayaprinting.javafx.control

import javafx.beans.property.SimpleIntegerProperty
import kotfx.bindings.intBindingOf


/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
class IntField : PromptTextField {

    val valueProperty = SimpleIntegerProperty().apply { bind(intBindingOf(textProperty()) { if (text.isNotEmpty()) text.toInt() else text.length }) }
    val value get() = valueProperty.value

    constructor() : super()

    constructor(promptText: String) : super(promptText)

    constructor(promptText: String, text: String) : super(promptText, text)

    init {
        textProperty().addListener { _, _, newValue ->
            if (newValue != null && !newValue.matches("\\d*".toRegex())) {
                text = newValue.replace("[^\\d]".toRegex(), "")
            }
        }
    }
}