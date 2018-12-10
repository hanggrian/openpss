package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.api.github.Asset
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.ListView
import ktfx.collections.toObservableList
import ktfx.jfoenix.jfxListView

class UpdateDialog(
    component: FxComponent,
    assets: List<Asset>
) : ResultableDialog<String>(component, R.string.download) {

    private val listView: ListView<Asset>

    override val focusedNode: Node? get() = listView

    init {
        listView = jfxListView<Asset> {
            items = assets.toObservableList()
            defaultButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull)
        }
    }

    override val nullableResult: String? get() = listView.selectionModel.selectedItem?.downloadUrl
}