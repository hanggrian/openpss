package com.hendraanggrian.openpss.scene

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.testfx.framework.junit.ApplicationTest

/**
 * Base test of all scene components.
 * In this app test, one instance of a node is created and tested accordingly.
 */
abstract class NodeTest<T : Node> : ApplicationTest() {

    abstract fun newInstance(): T

    protected lateinit var node: T

    override fun start(stage: Stage) {
        node = newInstance()
        stage.run {
            scene = Scene(when (node) {
                is Parent -> node as Parent
                else -> Pane(node)
            })
            show()
        }
    }
}