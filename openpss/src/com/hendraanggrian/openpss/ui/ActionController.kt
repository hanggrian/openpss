package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.control.ActionManager
import javafx.scene.Node
import ktfx.NodeManager
import java.net.URL
import java.util.ResourceBundle

@Suppress("LeakingThis")
open class ActionController : Controller(), ActionManager {

    val actionManager = object : NodeManager {
        override val collection = mutableListOf<Node>()
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        actionManager.onCreateActions()
    }
}