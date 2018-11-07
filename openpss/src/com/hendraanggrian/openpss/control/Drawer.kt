package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXDrawer
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import ktfx.coroutines.listener

class Drawer : JFXDrawer() {

    private val openedProperty = SimpleBooleanProperty(false)
    fun openedProperty(): BooleanProperty = openedProperty

    init {
        openedProperty.listener { _, _, value ->
            println(value)
        }
        setOnDrawerOpening { openedProperty.set(true) }
        setOnDrawerClosing { openedProperty.set(false) }
    }
}