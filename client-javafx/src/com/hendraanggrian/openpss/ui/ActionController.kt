package com.hendraanggrian.openpss.ui

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import ktfx.getValue
import ktfx.layouts.NodeInvokable
import ktfx.setValue
import java.net.URL
import java.util.ResourceBundle

open class ActionController : Controller() {

    private val titleProperty: StringProperty = SimpleStringProperty(null)
    fun titleProperty(): StringProperty = titleProperty
    var title: String? by titleProperty

    val actions = mutableListOf<Node>()

    private val actionInvokable = object : NodeInvokable {
        override fun <R : Node> R.invoke(): R = also { actions += it }
    }

    /** Override this function to add actions. */
    open fun NodeInvokable.onCreateActions() {
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        actionInvokable.onCreateActions()
    }
}