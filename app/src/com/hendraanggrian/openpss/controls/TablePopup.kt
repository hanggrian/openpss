package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.resources.Resourced
import javafx.scene.Node
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import ktfx.layouts.LayoutManager
import ktfx.layouts.button
import ktfx.layouts.tableView

abstract class TablePopup<T>(resourced: Resourced, titleId: String) : Popup<T>(resourced, titleId) {

    abstract val columns: List<TableColumn<T, *>>

    override val content: Node = tableView<T> {
        columnResizePolicy = CONSTRAINED_RESIZE_POLICY
        columns += this@TablePopup.columns
    }

    override fun LayoutManager<Node>.buttons() {
        button(getString(R.string.refresh)) {
        }
        button(getString(R.string.add)) {
        }
        button(getString(R.string.delete)) {
        }
    }
}