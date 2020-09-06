package com.hendraanggrian.openpss.ui

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import ktfx.getValue
import ktfx.layouts.NodeManager
import ktfx.setValue
import java.net.URL
import java.util.ResourceBundle

open class ActionController : Controller() {

    private val titleProperty: StringProperty = SimpleStringProperty(null)
    fun titleProperty(): StringProperty = titleProperty
    var title: String? by titleProperty

    val actions = mutableListOf<Node>()

    private val actionInvokable = object : NodeManager {
        override fun <C : Node> addChild(child: C): C = child.also { actions += it }
    }

    /** Override this function to add actions. */
    open fun NodeManager.onCreateActions() {
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        actionInvokable.onCreateActions()
    }
}
