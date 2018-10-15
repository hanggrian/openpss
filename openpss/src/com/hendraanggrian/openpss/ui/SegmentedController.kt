package com.hendraanggrian.openpss.ui

import javafx.scene.Node
import ktfx.NodeManager
import java.net.URL
import java.util.ResourceBundle

@Suppress("LeakingThis")
open class SegmentedController : Controller() {

    val leftActionManager = EmptyNodeManager()
    val rightActionManager = EmptyNodeManager()

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        leftActionManager.onCreateLeftActions()
        rightActionManager.onCreateRightActions()
    }

    /** Override this function to add left actions. */
    open fun NodeManager.onCreateLeftActions() {
    }

    /** Override this function to add right actions. */
    open fun NodeManager.onCreateRightActions() {
    }

    class EmptyNodeManager : NodeManager {

        override val collection = mutableListOf<Node>()
    }
}