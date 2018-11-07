package com.hendraanggrian.openpss.ui

import javafx.scene.Node
import ktfx.NodeInvokable
import java.net.URL
import java.util.ResourceBundle

open class ActionController : Controller() {

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