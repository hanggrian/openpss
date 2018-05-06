package com.hendraanggrian.openpss.ui

import javafx.scene.Node

open class SegmentedController : Controller() {

    open val leftButtons: List<Node> get() = emptyList()

    open val rightButtons: List<Node> get() = emptyList()
}