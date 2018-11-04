package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.content.ActionManager
import javafx.scene.Node
import ktfx.NodeInvokable
import java.net.URL
import java.util.ResourceBundle

open class ActionController : Controller(), ActionManager {

    val actions = mutableListOf<Node>()

    private val actionInvokable = object : NodeInvokable {
        override fun <R : Node> R.invoke(): R = also { actions += it }
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        actionInvokable.onCreateActions()
    }
}