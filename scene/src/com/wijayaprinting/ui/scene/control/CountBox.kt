@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import javafx.scene.control.ChoiceBox
import kotfx.annotations.SceneDsl
import kotfx.bindings.intBindingOf
import kotfx.collections.observableListOf
import kotfx.properties.IntProperty
import kotfx.properties.SimpleIntProperty
import kotfx.scene.ChildRoot
import kotfx.scene.ItemRoot
import kotfx.stringConverterOf

open class CountBox : ChoiceBox<Int>() {

    val countProperty: IntProperty = SimpleIntProperty()
    var desc: String = "items"

    init {
        items = observableListOf(20, 30, 40, 50)
        converter = stringConverterOf({ s -> s.toInt() }) { "$it $desc" }
        selectionModel.selectFirst()
        countProperty.bind(intBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem })
    }

    val count: Int get() = countProperty.get()
}

@JvmOverloads inline fun itemCountBox(noinline init: ((@SceneDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.itemCountBox(noinline init: ((@SceneDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.itemCountBox(noinline init: ((@SceneDsl CountBox).() -> Unit)? = null): CountBox = CountBox().apply { init?.invoke(this) }.add()