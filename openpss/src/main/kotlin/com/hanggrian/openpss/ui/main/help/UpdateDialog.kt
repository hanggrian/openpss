package com.hanggrian.openpss.ui.main.help

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.popup.dialog.ResultableDialog
import javafx.scene.Node
import ktfx.collections.toObservableList
import ktfx.jfoenix.layouts.jfxListView

class UpdateDialog(context: Context, assets: List<GitHubApi.Asset>) :
    ResultableDialog<String>(context, R.string_download) {
    private val listView =
        jfxListView<GitHubApi.Asset> {
            items = assets.toObservableList()
            defaultButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull)
        }

    override val focusedNode: Node
        get() = listView

    override val nullableResult: String?
        get() = listView.selectionModel.selectedItem?.downloadUrl
}
