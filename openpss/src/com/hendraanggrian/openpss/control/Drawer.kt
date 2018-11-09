package com.hendraanggrian.openpss.control

import com.jfoenix.controls.JFXDrawer
import com.jfoenix.controls.events.JFXDrawerEvent
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventHandler

class Drawer : JFXDrawer() {

    private val openingProperty = SimpleBooleanProperty(false)
    fun openingProperty(): BooleanProperty = openingProperty

    init {
        onDrawerOpening = null
        onDrawerClosing = null
    }

    override fun setOnDrawerOpening(onDrawerOpening: EventHandler<JFXDrawerEvent>?) = super.setOnDrawerOpening {
        onDrawerOpening?.handle(it)
        openingProperty.set(true)
    }

    override fun setOnDrawerClosing(onDrawerClosing: EventHandler<JFXDrawerEvent>?) = super.setOnDrawerClosing {
        onDrawerClosing?.handle(it)
        openingProperty.set(false)
    }
}