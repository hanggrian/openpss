package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import javafx.scene.control.ListView
import ktfx.collections.toObservableList
import ktfx.jfoenix.jfxListView

class UpdateDialog(
    context: Context,
    assets: List<GitHubApi.Asset>
) : ResultableDialog<String>(context, R.string.download) {

    private val listView: ListView<GitHubApi.Asset>

    override val focusedNode: Node? get() = listView

    init {
        listView = jfxListView<GitHubApi.Asset> {
            items = assets.toObservableList()
            defaultButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull)
        }
    }

    override val nullableResult: String? get() = listView.selectionModel.selectedItem?.downloadUrl
}
