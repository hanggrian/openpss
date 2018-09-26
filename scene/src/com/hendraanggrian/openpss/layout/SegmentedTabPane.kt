package com.hendraanggrian.openpss.layout

import com.hendraanggrian.openpss.control.StretchableToggleButton
import com.hendraanggrian.openpss.scene.R
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.Pane
import ktfx.application.later
import ktfx.coroutines.listener
import ktfx.scene.layout.paddingTop
import org.controlsfx.control.SegmentedButton

class SegmentedTabPane : TabPane() {

    companion object {
        const val STRETCH_POINT = 1100.0
    }

    var header: SegmentedButton = SegmentedButton()
    var isTextStretchable: Boolean = false

    init {
        stylesheets += javaClass.getResource(R.style.segmentedtabpane).toExternalForm()
        later { paddingTop = -(lookup(".tab-header-area") as Pane).height }
        header.toggleGroup.run {
            selectedToggleProperty().listener { _, oldValue, value ->
                when (value) {
                    null -> selectToggle(oldValue)
                    else -> selectionModel.select(toggles.indexOf(value))
                }
            }
        }
        selectionModel.selectedIndexProperty().listener { _, _, value ->
            if (header.buttons.isNotEmpty()) {
                header.toggleGroup.selectToggle(header.buttons[value.toInt()])
            }
        }
        populate(tabs)
        tabs.listener<Tab> { change ->
            change.next()
            when {
                change.wasAdded() -> {
                    populate(change.addedSubList)
                    if (change.from == 0) {
                        header.buttons.first().isSelected = true
                    }
                }
                else -> header.buttons -= header.buttons.filter { button -> button.text in change.addedSubList.map { it.text } }
            }
        }
    }

    private fun populate(tabs: Collection<Tab>) {
        header.buttons += tabs.map {
            when {
                isTextStretchable -> StretchableToggleButton(STRETCH_POINT, it.text, it.graphic)
                else -> ToggleButton(it.text, it.graphic)
            }
        }
    }
}