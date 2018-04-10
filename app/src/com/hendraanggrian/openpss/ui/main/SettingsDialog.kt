package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.GlobalSettings
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.io.properties.SettingsFile
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.stringCell
import javafx.scene.control.Dialog
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.image.ImageView
import kotlinx.nosql.equal
import kotlinx.nosql.update
import ktfx.collections.toObservableList
import ktfx.coroutines.onEditCommit
import ktfx.layouts.checkBox
import ktfx.layouts.columns
import ktfx.layouts.label
import ktfx.layouts.tableView
import ktfx.layouts.vbox
import ktfx.scene.control.closeButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle
import ktfx.scene.control.textFieldCellFactory

class SettingsDialog(resourced: Resourced, showGlobalSettings: Boolean) : Dialog<Unit>(), Resourced by resourced {

    init {
        headerTitle = getString(R.string.settings)
        graphicIcon = ImageView(R.image.ic_settings)
        dialogPane.content = vbox {
            spacing = 16.0
            label(getString(R.string.local_settings)) { font = getFont(R.font.opensans_bold, 16) }
            vbox {
                spacing = 8.0
                label(getString(R.string.invoice)) { font = getFont(R.font.opensans_bold) }
                checkBox(getString(R.string.quick_select_customer_when_adding_invoice)) {
                    selectedProperty().bindBidirectional(SettingsFile.INVOICE_QUICK_SELECT_CUSTOMER)
                }
            }
        }
        if (showGlobalSettings) dialogPane.expandableContent = vbox {
            spacing = 8.0
            //label(getString(R.string.config_detail))
            tableView(transaction { GlobalSettings.find().toObservableList() }!!) {
                prefWidth = 480.0
                isEditable = true
                columnResizePolicy = CONSTRAINED_RESIZE_POLICY
                columns {
                    getString(R.string.key)<String> { stringCell { transaction { key }!! } }
                    getString(R.string.value)<String> {
                        stringCell { transaction { value }!! }
                        textFieldCellFactory()
                        onEditCommit {
                            transaction {
                                GlobalSettings.find { key.equal(it.rowValue.key) }.projection { value }
                                    .update(it.newValue)
                                it.rowValue.value = it.newValue
                            }
                        }
                    }
                }
            }
        }
        closeButton()
    }
}