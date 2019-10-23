package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.data.Asset
import com.hendraanggrian.openpss.ui.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.ListView
import ktfx.collections.toObservableList
import ktfx.jfoenix.jfxListView

class UpdateDialog(
    component: FxComponent,
    assets: List<Asset>
) : ResultableDialog<String>(component, R2.string.download) {

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
