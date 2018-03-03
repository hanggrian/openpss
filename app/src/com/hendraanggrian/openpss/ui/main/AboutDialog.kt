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
import kotfx.beans.binding.and
import kotfx.beans.binding.booleanBindingOf
import kotfx.beans.binding.stringBindingOf
import kotfx.collections.toObservableList
import kotfx.coroutines.onAction
import kotfx.layouts.button
import kotfx.layouts.hbox
import kotfx.layouts.imageView
import kotfx.layouts.label
import kotfx.layouts.text
import kotfx.layouts.textFlow
import kotfx.layouts.titledPane
import kotfx.layouts.vbox
import kotfx.listeners.cellFactory
import kotfx.scene.control.closeButton
import kotfx.scene.control.customButton
import kotfx.scene.control.icon
import kotfx.scene.layout.paddings
import kotfx.scene.layout.prefSize
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
                    text("Open ") { font = loadFont(getResourceString(R.font.lato_bold), 24.0) }
                    text("Printing Sales System") { font = loadFont(getResourceString(R.font.lato_light), 24.0) }
                }
                text("${getString(R.string.version)} $VERSION") { font = loadFont(getResourceString(R.font.lato_regular), 12.0) } topMargin 2
                text(getString(R.string.about_notice)) { font = loadFont(getResourceString(R.font.lato_bold), 12.0) } topMargin 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") { font = loadFont(getResourceString(R.font.lato_bold), 12.0) }
                    text("JavaFX") { font = loadFont(getResourceString(R.font.lato_regular), 12.0) }
                } topMargin 4
                textFlow {
                    text("${getString(R.string.author)}  ") { font = loadFont(getResourceString(R.font.lato_bold), 12.0) }
                    text("Hendra Anggrian") { font = loadFont(getResourceString(R.font.lato_regular), 12.0) }
                } topMargin 4
                hbox {
                    button("GitHub") { onAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting")) } }
                    button(getString(R.string.check_for_updates)) { onAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting/releases")) } } marginLeft 8
                } topMargin 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        dialogPane.expandableContent = hbox {
            listView = kotfx.layouts.listView {
                prefSize(height = 256)
                items = License.values().toObservableList()
                cellFactory {
                    onUpdateItem { license, empty ->
                        text = null
                        graphic = null
                        if (license != null && !empty) graphic = kotfx.layouts.vbox {
                            label(license.repo) { font = loadFont(getResourceString(R.font.lato_regular), 12.0) }
                            label(license.owner) { font = loadFont(getResourceString(R.font.lato_bold), 12.0) }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotfx.layouts.textArea {
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