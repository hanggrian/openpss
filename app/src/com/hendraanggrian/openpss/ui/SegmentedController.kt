package com.hendraanggrian.openpss.ui

import javafx.scene.Node

open class SegmentedController : Controller() {

    open val leftSegment: List<Node> get() = emptyList()

    open val rightSegment: List<Node> get() = emptyList()
}