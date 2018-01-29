@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ChoiceBox
import kotfx.*

open class CountBox : ChoiceBox<Int>() {

    val countProperty: IntegerProperty = SimpleIntegerProperty()
    var desc: String = "items"

    init {
        items = observableListOf(20, 40, 60, 80)
        converter = stringConverterOf({ s -> s.toInt() }) { "$it $desc" }
        selectionModel.selectFirst()
        countProperty.bind(intBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem })
    }

    val count: Int get() = countProperty.get()
}

@JvmOverloads inline fun itemCountBox(noinline init: ((@LayoutDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.itemCountBox(noinline init: ((@LayoutDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.itemCountBox(noinline init: ((@LayoutDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()