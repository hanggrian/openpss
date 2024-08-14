package com.hanggrian.openpss.ui

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.Node
import ktfx.getValue
import ktfx.layouts.NodeContainer
import ktfx.setValue
import java.net.URL
import java.util.ResourceBundle

open class ActionController : Controller() {
    val titleProperty: StringProperty = SimpleStringProperty(null)
    var title: String? by titleProperty

    val actions = mutableListOf<Node>()

    private val actionInvokable =
        object : NodeContainer {
            override fun <T : Node> addChild(child: T): T = child.also { actions += it }
        }

    /** Override this function to add actions. */
    open fun NodeContainer.onCreateActions() {}

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        actionInvokable.onCreateActions()
    }
}
