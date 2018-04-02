@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import ktfx.beans.binding.intBindingOf
import ktfx.beans.value.getValue
import ktfx.collections.observableListOf
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.listeners.converter

open class CountBox : ChoiceBox<Int>() {

    val countProperty: IntegerProperty = SimpleIntegerProperty()
    val count: Int by countProperty

    var desc: String = "items"

    init {
        items = observableListOf(20, 30, 40, 50)
        converter {
            fromString { s -> s.toInt() }
            toString { "$it $desc" }
        }
        selectionModel.selectFirst()
        @Suppress("LeakingThis") countProperty.bind(intBindingOf(valueProperty()) { selectionModel.selectedItem })
    }
}

inline fun countBox(): CountBox = countBox { }

inline fun countBox(
    init: (@LayoutDsl CountBox).() -> Unit
): CountBox = CountBox().apply(init)

inline fun LayoutManager<Node>.countBox(): CountBox = countBox { }

inline fun LayoutManager<Node>.countBox(
    init: (@LayoutDsl CountBox).() -> Unit
): CountBox = com.hendraanggrian.openpss.scene.control.countBox(init).add()