package com.hendraanggrian.openpss.ui

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.SelectionModel
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel

interface Selectable<T> {

    val selectionModel: SelectionModel<T>

    val selectedIndexProperty: ReadOnlyIntegerProperty get() = selectionModel.selectedIndexProperty()

    val selectedIndex: Int get() = selectedIndexProperty.value

    val selectedProperty: ReadOnlyObjectProperty<T> get() = selectionModel.selectedItemProperty()

    val selected: T? get() = selectedProperty.value

    val selectedBinding: BooleanBinding get() = selectedProperty.isNotNull

    fun reselect(index: Int) = selectionModel.clearAndSelect(index)

    fun select(index: Int) = selectionModel.select(index)

    fun select(item: T?) = selectionModel.select(item)

    fun clearSelection(index: Int = -1) = when (index) {
        -1 -> selectionModel.clearSelection()
        else -> selectionModel.clearSelection(index)
    }

    fun isSelected(index: Int): Boolean = selectionModel.isSelected(index)

    fun isEmpty(): Boolean = selectionModel.isEmpty

    fun selectPrevious() = selectionModel.selectPrevious()

    fun selectNext() = selectionModel.selectNext()

    fun selectFirst() = selectionModel.selectFirst()

    fun selectLast() = selectionModel.selectLast()
}

interface Selectable2<T> {

    val selectionModel2: SelectionModel<T>

    val selectedIndexProperty2: ReadOnlyIntegerProperty get() = selectionModel2.selectedIndexProperty()

    val selectedIndex2: Int get() = selectedIndexProperty2.value

    val selectedProperty2: ReadOnlyObjectProperty<T> get() = selectionModel2.selectedItemProperty()

    val selected2: T? get() = selectedProperty2.value

    val selectedBinding2: BooleanBinding get() = selectedProperty2.isNotNull

    fun reselect2(index: Int) = selectionModel2.clearAndSelect(index)

    fun select2(index: Int) = selectionModel2.select(index)

    fun select2(item: T?) = selectionModel2.select(item)

    fun clearSelection2(index: Int = -1) = when (index) {
        -1 -> selectionModel2.clearSelection()
        else -> selectionModel2.clearSelection(index)
    }

    fun isSelected2(index: Int): Boolean = selectionModel2.isSelected(index)

    fun isEmpty2(): Boolean = selectionModel2.isEmpty

    fun selectPrevious2() = selectionModel2.selectPrevious()

    fun selectNext2() = selectionModel2.selectNext()

    fun selectFirst2() = selectionModel2.selectFirst()

    fun selectLast2() = selectionModel2.selectLast()
}

interface Selectable3<T> {

    val selectionModel3: SelectionModel<T>

    val selectedIndexProperty3: ReadOnlyIntegerProperty get() = selectionModel3.selectedIndexProperty()

    val selectedIndex3: Int get() = selectedIndexProperty3.value

    val selectedProperty3: ReadOnlyObjectProperty<T> get() = selectionModel3.selectedItemProperty()

    val selected3: T? get() = selectedProperty3.value

    val selectedBinding3: BooleanBinding get() = selectedProperty3.isNotNull

    fun reselect3(index: Int) = selectionModel3.clearAndSelect(index)

    fun select3(index: Int) = selectionModel3.select(index)

    fun select3(item: T?) = selectionModel3.select(item)

    fun clearSelection3(index: Int = -1) = when (index) {
        -1 -> selectionModel3.clearSelection()
        else -> selectionModel3.clearSelection(index)
    }

    fun isSelected3(index: Int): Boolean = selectionModel3.isSelected(index)

    fun isEmpty3(): Boolean = selectionModel3.isEmpty

    fun selectPrevious3() = selectionModel3.selectPrevious()

    fun selectNext3() = selectionModel3.selectNext()

    fun selectFirst3() = selectionModel3.selectFirst()

    fun selectLast3() = selectionModel3.selectLast()
}

interface TreeSelectable<T> {

    val selectionModel: TreeTableViewSelectionModel<T>

    val selected: TreeItem<T>? get() = selectionModel.selectedItem

    val selecteds: ObservableList<TreeItem<T>> get() = selectionModel.selectedItems

    val selectedProperty: ReadOnlyObjectProperty<TreeItem<T>> get() = selectionModel.selectedItemProperty()

    val selectedBinding: BooleanBinding get() = selectedProperty.isNotNull

    fun select(index: Int) = selectionModel.select(index)

    fun clearSelection() = selectionModel.clearSelection()
}