@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ChoiceBox
import kotfx.annotations.LayoutDsl
import kotfx.beans.binding.intBindingOf
import kotfx.collections.observableListOf
import kotfx.layout.ChildManager
import kotfx.layout.ItemManager
import kotfx.listeners.converter

open class CountBox : ChoiceBox<Int>() {

    val countProperty: IntegerProperty = SimpleIntegerProperty()
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

    val count: Int get() = countProperty.get()
}

inline fun itemCountBox(noinline init: ((@LayoutDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }
inline fun ChildManager.itemCountBox(noinline init: ((@LayoutDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()
inline fun ItemManager.itemCountBox(noinline init: ((@LayoutDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()