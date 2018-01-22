@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.wijayaprinting.ui.scene.control

import com.wijayaprinting.ui.scene.control.ItemCountBox.Count.values
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ChoiceBox
import kotfx.*

open class ItemCountBox : ChoiceBox<ItemCountBox.Count>() {

    val countProperty = SimpleIntegerProperty()

    init {
        items = observableListOf(*values())
        selectionModel.select(0)

        countProperty bind intBindingOf(selectionModel.selectedItemProperty()) { selectionModel.selectedItem.count }
    }

    var count: Int
        get() = countProperty.get()
        set(value) = countProperty.set(value)

    enum class Count(internal val count: Int) {
        TWENTY(20),
        FOURTY(40),
        SIXTY(60),
        EIGHTY(80),
        HUNDRED(100);

        override fun toString(): String = "$count items"
    }
}

@JvmOverloads inline fun itemCountBox(noinline init: ((@KotfxDsl ItemCountBox).() -> Unit)? = null): ItemCountBox = ItemCountBox().apply { init?.invoke(this) }
@JvmOverloads inline fun ChildRoot.itemCountBox(noinline init: ((@KotfxDsl ItemCountBox).() -> Unit)? = null): ItemCountBox = ItemCountBox().apply { init?.invoke(this) }.add()
@JvmOverloads inline fun ItemRoot.itemCountBox(noinline init: ((@KotfxDsl ItemCountBox).() -> Unit)? = null): ItemCountBox = ItemCountBox().apply { init?.invoke(this) }.add()