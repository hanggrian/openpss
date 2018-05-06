package com.hendraanggrian.openpss.layouts

import com.hendraanggrian.openpss.scene.R
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.Pane
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.scene.layout.paddingTop
import org.controlsfx.control.SegmentedButton

class HiddenTabPane : TabPane() {

    val segmentedButton: SegmentedButton = SegmentedButton()

    init {
        stylesheets += javaClass.getResource(R.style.hiddentabpane).toExternalForm()
        later { paddingTop = -(lookup(".tab-header-area") as Pane).height }
        segmentedButton.toggleGroup.run {
            selectedToggleProperty().listener { _, oldValue, value ->
                when (value) {
                    null -> selectToggle(oldValue)
                    else -> selectionModel.select(toggles.indexOf(value))
                }
            }
        }
        populate(tabs)
        tabs.listener<Tab> { change ->
            change.next()
            segmentedButton.run {
                when {
                    change.wasAdded() -> {
                        populate(change.addedSubList)
                        if (change.from == 0) buttons.first().isSelected = true
                    }
                    else -> buttons -= buttons.filter { it.text in change.addedSubList.map { it.text } }
                }
            }
        }
    }

    private fun populate(tabs: Collection<Tab>) {
        segmentedButton.buttons += tabs.map {
            when (it.graphic) {
                null -> ToggleButton(it.text)
                else -> ToggleButton(it.text, it.graphic)
            }
        }
    }
}