@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ChoiceBox
import kotfx.annotations.SceneDsl
import kotfx.bindings.intBindingOf
import kotfx.collections.observableListOf
import kotfx.scene.ChildManager
import kotfx.scene.ItemManager
import kotfx.stringConverterOf

open class CountBox : ChoiceBox<Int>() {

    val countProperty: IntegerProperty = SimpleIntegerProperty()
    var desc: String = "items"

    init {
        items = observableListOf(20, 30, 40, 50)
        converter = stringConverterOf({ "$it $desc" }) { s -> s.toInt() }
        selectionModel.selectFirst()
        countProperty.bind(intBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem })
    }

    val count: Int get() = countProperty.get()
}

@JvmOverloads inline fun itemCountBox(noinline init: ((@SceneDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildManager.itemCountBox(noinline init: ((@SceneDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemManager.itemCountBox(noinline init: ((@SceneDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()