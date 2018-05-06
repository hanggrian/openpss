package com.hendraanggrian.openpss.ui

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.SelectionModel
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel

/** To avoid repetitive code, this interface contains common operation of selection model. */
interface Selectable<T> {

    val selectionModel: SelectionModel<T>

    val selected: T? get() = selectionModel.selectedItem

    val selectedIndex: Int get() = selectionModel.selectedIndex

    val selectedProperty: ReadOnlyObjectProperty<T> get() = selectionModel.selectedItemProperty()

    val selectedBinding: BooleanBinding get() = selectedProperty.isNotNull

    fun clearSelection() = selectionModel.clearSelection()

    fun reselect(index: Int) = selectionModel.clearAndSelect(index)
}

/** Some components may have 2 selection models (e.g.: `CustomerController`, `InvoiceController`, etc.). */
interface Selectable2<T> {

    val selectionModel2: SelectionModel<T>

    val selected2: T? get() = selectionModel2.selectedItem

    val selectedIndex2: Int get() = selectionModel2.selectedIndex

    val selectedProperty2: ReadOnlyObjectProperty<T> get() = selectionModel2.selectedItemProperty()

    val selectedBinding2: BooleanBinding get() = selectedProperty2.isNotNull

    fun clearSelection2() = selectionModel2.clearSelection()

    fun reselect2(index: Int) = selectionModel2.clearAndSelect(index)
}

interface TreeSelectable<T> {

    val selectionModel: TreeTableViewSelectionModel<T>

    val selected: TreeItem<T>? get() = selectionModel.selectedItem

    val selecteds: ObservableList<TreeItem<T>> get() = selectionModel.selectedItems

    val selectedProperty: ReadOnlyObjectProperty<TreeItem<T>> get() = selectionModel.selectedItemProperty()

    val selectedBinding: BooleanBinding get() = selectedProperty.isNotNull

    fun clearSelection() = selectionModel.clearSelection()
}