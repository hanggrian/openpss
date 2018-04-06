package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.BuildConfig.WEBSITE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.Resourced
import com.hendraanggrian.openpss.utils.browseUrl
import com.hendraanggrian.openpss.utils.getFont
import com.hendraanggrian.openpss.utils.onActionFilter
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.image.Image
import ktfx.beans.binding.and
import ktfx.beans.binding.booleanBindingOf
import ktfx.collections.toObservableList
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.text
import ktfx.layouts.textArea
import ktfx.layouts.textFlow
import ktfx.layouts.titledPane
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.scene.control.button
import ktfx.scene.control.closeButton
import ktfx.scene.control.icon
import ktfx.scene.layout.paddingAll

class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Resourced by resourced {

    private lateinit var licenseList: ListView<License>

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        dialogPane.content = hbox {
            paddingAll = 48.0
            imageView(Image(R.image.logo_launcher))
            vbox {
                alignment = CENTER_LEFT
                textFlow {
                    "Open " { font = getFont(R.font.opensans_bold, 24) }
                    "Printing Sales System" { font = getFont(R.font.opensans_light, 24) }
                }
                text("${getString(R.string.version)} $VERSION") {
                    font = getFont(R.font.opensans_regular, 12)
                } marginTop 2.0
                text(getString(R.string.about_notice)) {
                    font = getFont(R.font.opensans_bold, 12)
                } marginTop 20.0
                textFlow {
                    "${getString(R.string.powered_by)}  " {
                        font = getFont(R.font.opensans_bold, 12)
                    }
                    "JavaFX" { font = getFont(R.font.opensans_regular, 12) }
                } marginTop 4.0
                textFlow {
                    "${getString(R.string.author)}  " {
                        font = getFont(R.font.opensans_bold, 12)
                    }
                    "Hendra Anggrian" { font = getFont(R.font.opensans_regular, 12) }
                } marginTop 4.0
                hbox {
                    button("GitHub") { onAction { browseUrl(WEBSITE) } }
                    button(getString(R.string.check_for_updates)) {
                        onAction { browseUrl("$WEBSITE/releases") }
                    } marginLeft 8.0
                } marginTop 20.0
            } marginLeft 48.0
        }
        dialogPane.expandableContent = hbox {
            titledPane(getString(R.string.open_source_software)) {
                isCollapsible = false
                licenseList = listView {
                    prefHeight = 256.0
                    items = License.values().toObservableList()
                    cellFactory {
                        onUpdate { license, empty ->
                            if (license != null && !empty) graphic = ktfx.layouts.vbox {
                                label(license.repo) {
                                    font = getFont(R.font.opensans_regular, 12)
                                }
                                label(license.owner) {
                                    font = getFont(R.font.opensans_bold, 12)
                                }
                            }
                        }
                    }
                }
            }
            titledPane(getString(R.string.license)) {
                isCollapsible = false
                textArea {
                    prefHeight = 256.0
                    isEditable = false
                    text = getString(R.string.select_license)
                    licenseList.selectionModel.selectedItemProperty().listener { _, _, license ->
                        text = license?.getContent() ?: getString(R.string.select_license)
                    }
                }
            }
        }
        button("Homepage", CANCEL_CLOSE) {
            visibleProperty().bind(dialogPane.expandedProperty() and
                booleanBindingOf(licenseList.selectionModel.selectedIndexProperty()) {
                    licenseList.selectionModel.selectedItem != null
                })
            onActionFilter { browseUrl(licenseList.selectionModel.selectedItem.homepage) }
        }
        closeButton()
    }
}