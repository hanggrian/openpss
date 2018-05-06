package com.hendraanggrian.openpss.layouts

import javafx.beans.DefaultProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.Priority.ALWAYS
import ktfx.layouts.LayoutManager
import ktfx.layouts._VBox
import ktfx.layouts.hbox
import ktfx.layouts.toolBar
import ktfx.scene.layout.paddingAll

@DefaultProperty("tabs")
class SegmentedTabPane : _VBox(0.0) {

    private val tabPane: HiddenTabPane = HiddenTabPane()
    private lateinit var leftBox: HBox
    private lateinit var rightBox: HBox

    init {
        toolBar {
            paddingAll = 10.0
            leftBox = buttons(CENTER_LEFT)
            tabPane.segmentedButton.add()
            rightBox = buttons(CENTER_RIGHT)
        }
        tabPane.add() vpriority ALWAYS
    }

    val leftButtons: ObservableList<Node> get() = leftBox.children

    val rightButtons: ObservableList<Node> get() = rightBox.children

    val tabs: ObservableList<Tab> get() = tabPane.tabs

    val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel

    private fun LayoutManager<Node>.buttons(pos: Pos): HBox = hbox(8.0) {
        alignment = pos
        setHgrow(this, ALWAYS)
    }
}