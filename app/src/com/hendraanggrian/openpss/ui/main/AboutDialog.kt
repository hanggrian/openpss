package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.util.getResourceString
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.text.Font.loadFont
import kotlinfx.beans.binding.and
import kotlinfx.beans.binding.booleanBindingOf
import kotlinfx.beans.binding.stringBindingOf
import kotlinfx.collections.toObservableList
import kotlinfx.coroutines.onAction
import kotlinfx.layouts.button
import kotlinfx.layouts.hbox
import kotlinfx.layouts.imageView
import kotlinfx.layouts.label
import kotlinfx.layouts.text
import kotlinfx.layouts.textFlow
import kotlinfx.layouts.titledPane
import kotlinfx.layouts.vbox
import kotlinfx.listeners.cellFactory
import kotlinfx.scene.control.closeButton
import kotlinfx.scene.control.customButton
import kotlinfx.scene.control.icon
import kotlinfx.scene.layout.paddings
import kotlinfx.scene.layout.prefSize
import java.awt.Desktop.getDesktop
import java.net.URI

class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Resourced by resourced {

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        dialogPane.content = hbox {
            paddings = 48
            imageView(Image(R.image.logo_launcher))
            vbox {
                alignment = CENTER_LEFT
                textFlow {
                    text("Open ") { font = loadFont(getResourceString(R.font.opensans_bold), 24.0) }
                    text("Printing Sales System") { font = loadFont(getResourceString(R.font.opensans_light), 24.0) }
                }
                text("${getString(R.string.version)} $VERSION") { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) } topMargin 2
                text(getString(R.string.about_notice)) { font = loadFont(getResourceString(R.font.opensans_bold), 12.0) } topMargin 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") { font = loadFont(getResourceString(R.font.opensans_bold), 12.0) }
                    text("JavaFX") { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) }
                } topMargin 4
                textFlow {
                    text("${getString(R.string.author)}  ") { font = loadFont(getResourceString(R.font.opensans_bold), 12.0) }
                    text("Hendra Anggrian") { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) }
                } topMargin 4
                hbox {
                    button("GitHub") { onAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting")) } }
                    button(getString(R.string.check_for_updates)) { onAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting/releases")) } } marginLeft 8
                } topMargin 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        dialogPane.expandableContent = hbox {
            listView = kotlinfx.layouts.listView {
                prefSize(height = 256)
                items = License.values().toObservableList()
                cellFactory {
                    onUpdateItem { license, empty ->
                        if (license != null && !empty) graphic = kotlinfx.layouts.vbox {
                            label(license.repo) { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) }
                            label(license.owner) { font = loadFont(getResourceString(R.font.opensans_bold), 12.0) }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotlinfx.layouts.textArea {
                prefSize(height = 256)
                isEditable = false
                textProperty().bind(stringBindingOf(listView.selectionModel.selectedIndexProperty()) {
                    listView.selectionModel.selectedItem?.content ?: getString(R.string.select_license)
                })
            }) { isCollapsible = false }
        }
        customButton("Homepage", CANCEL_CLOSE) {
            visibleProperty().bind(dialogPane.expandedProperty() and booleanBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem != null })
            addEventFilter(ACTION) {
                it.consume()
                getDesktop().browse(URI(listView.selectionModel.selectedItem.homepage))
            }
        }
        closeButton()
    }
}