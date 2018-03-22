package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.BuildConfig.WEBSITE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.util.browseUrl
import com.hendraanggrian.openpss.util.getResourceString
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.text.Font.loadFont
import ktfx.beans.binding.and
import ktfx.beans.binding.booleanBindingOf
import ktfx.beans.binding.stringBindingOf
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.titledPane
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.scene.control.closeButton
import ktfx.scene.control.customButton
import ktfx.scene.control.icon
import ktfx.scene.layout.heightPref
import ktfx.scene.layout.paddings

class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Resourced by resourced {

    private lateinit var licenseList: ListView<License>

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
                text("${getString(R.string.version)} $VERSION") {
                    font = loadFont(getResourceString(R.font.opensans_regular), 12.0)
                } marginTop 2
                text(getString(R.string.about_notice)) {
                    font = loadFont(getResourceString(R.font.opensans_bold), 12.0)
                } marginTop 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") {
                        font = loadFont(getResourceString(R.font.opensans_bold), 12.0)
                    }
                    text("JavaFX") { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) }
                } marginTop 4
                textFlow {
                    text("${getString(R.string.author)}  ") {
                        font = loadFont(getResourceString(R.font.opensans_bold), 12.0)
                    }
                    text("Hendra Anggrian") { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) }
                } marginTop 4
                hbox {
                    button("GitHub") { onAction { browseUrl(WEBSITE) } }
                    button(getString(R.string.check_for_updates)) {
                        onAction { browseUrl("$WEBSITE/releases") }
                    } marginLeft 8
                } marginTop 20
            } marginLeft 48
        }
        dialogPane.expandableContent = hbox {
            licenseList = ktfx.layouts.listView {
                heightPref = 256
                items = License.values().toObservableList()
                cellFactory {
                    onUpdate { license, empty ->
                        if (license != null && !empty) graphic = ktfx.layouts.vbox {
                            label(license.repo) { font = loadFont(getResourceString(R.font.opensans_regular), 12.0) }
                            label(license.owner) { font = loadFont(getResourceString(R.font.opensans_bold), 12.0) }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), licenseList) { isCollapsible = false }
            titledPane(getString(R.string.license), ktfx.layouts.textArea {
                heightPref = 256
                isEditable = false
                textProperty().bind(stringBindingOf(licenseList.selectionModel.selectedIndexProperty()) {
                    licenseList.selectionModel.selectedItem?.content ?: getString(R.string.select_license)
                })
            }) { isCollapsible = false }
        }
        customButton("Homepage", CANCEL_CLOSE) {
            visibleProperty().bind(dialogPane.expandedProperty() and
                booleanBindingOf(licenseList.selectionModel.selectedIndexProperty()) {
                    licenseList.selectionModel.selectedItem != null
                })
            addEventFilter(ACTION) {
                it.consume()
                browseUrl(licenseList.selectionModel.selectedItem.homepage)
            }
        }
        closeButton()
    }
}