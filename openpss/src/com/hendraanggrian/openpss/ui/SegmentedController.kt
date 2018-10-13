package com.hendraanggrian.openpss.ui

import javafx.scene.Node
import ktfx.layouts.LayoutManager
import java.net.URL
import java.util.ResourceBundle

@Suppress("LeakingThis")
open class SegmentedController : Controller() {

    val leftActionManager = EmptyLayoutManager()
    val rightActionManager = EmptyLayoutManager()

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        leftActionManager.onCreateLeftActions()
        rightActionManager.onCreateRightActions()
    }

    /** Override this function to add left actions. */
    open fun LayoutManager<Node>.onCreateLeftActions() {
    }

    /** Override this function to add right actions. */
    open fun LayoutManager<Node>.onCreateRightActions() {
    }

    class EmptyLayoutManager : LayoutManager<Node> {

        override val childs = mutableListOf<Node>()
    }
}