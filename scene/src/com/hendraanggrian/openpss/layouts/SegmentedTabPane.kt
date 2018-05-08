package com.hendraanggrian.openpss.layouts

import javafx.beans.DefaultProperty
import javafx.collections.ObservableList
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.SelectionModel
import javafx.scene.control.Tab
import javafx.scene.layout.HBox
import javafx.scene.layout.HBox.setHgrow
import javafx.scene.layout.Priority.ALWAYS
import ktfx.layouts._VBox
import ktfx.layouts.hbox
import ktfx.layouts.toolBar
import ktfx.scene.layout.paddingAll

@DefaultProperty("tabs")
class SegmentedTabPane : _VBox(0.0) {

    private val tabPane: HiddenTabPane = HiddenTabPane()
    private lateinit var leftBar: HBox
    private lateinit var rightBar: HBox

    init {
        toolBar {
            paddingAll = 16.0
            leftBar = hbox(8.0) {
                alignment = CENTER_LEFT
                setHgrow(this, ALWAYS)
            }
            tabPane.segmentedButton.add()
            rightBar = hbox(8.0) {
                alignment = CENTER_RIGHT
                setHgrow(this, ALWAYS)
            }
        }
        tabPane.add() vpriority ALWAYS
    }

    val leftButtons: ObservableList<Node> get() = leftBar.children

    val rightButtons: ObservableList<Node> get() = rightBar.children

    val tabs: ObservableList<Tab> get() = tabPane.tabs

    val selectionModel: SelectionModel<Tab> get() = tabPane.selectionModel
}