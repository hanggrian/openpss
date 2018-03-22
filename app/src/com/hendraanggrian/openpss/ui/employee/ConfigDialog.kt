package com.hendraanggrian.openpss.ui.employee

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schema.Configs
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.ui.Resourced
import javafx.scene.control.Dialog
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.beans.property.toProperty
import ktfx.collections.toObservableList
import ktfx.coroutines.onEditCommit
import ktfx.layouts.columns
import ktfx.layouts.label
import ktfx.layouts.tableView
import ktfx.layouts.vbox
import ktfx.scene.control.closeButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.textFieldCellFactory
import ktfx.scene.layout.spacings

class ConfigDialog(resourced: Resourced) : Dialog<Unit>(), Resourced by resourced {

    init {
        headerTitle = getString(R.string.config)
        graphicIcon = ImageView(R.image.ic_config)
        dialogPane.content = vbox {
            spacings = 8
            label(getString(R.string.config_detail))
            tableView(transaction { Configs.find().toObservableList() }!!) {
                isEditable = true
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                columns {
                    column<String> { setCellValueFactory { transaction { it.value.key }.toProperty() } }
                    column<String> {
                        setCellValueFactory { transaction { it.value.value }.toProperty() }
                        textFieldCellFactory()
                        onEditCommit {
                            transaction {
                                Configs.find { key.equal(it.rowValue.key) }.projection { value }.update(it.newValue)
                            }
                        }
                    }
                }
            }
        }
        closeButton()
    }
}