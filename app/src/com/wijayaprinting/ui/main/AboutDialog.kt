package com.wijayaprinting.ui.main

import com.wijayaprinting.BuildConfig.VERSION
import com.wijayaprinting.R
import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.ui.scene.control.GraphicListCell
import com.wijayaprinting.util.getFont
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.Node
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.image.Image
import kotfx.bindings.and
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.stringBindingOf
import kotfx.collections.toObservableList
import kotfx.dialogs.button
import kotfx.dialogs.content
import kotfx.dialogs.expandableContent
import kotfx.dialogs.icon
import kotfx.scene.*
import java.awt.Desktop.getDesktop
import java.net.URI

class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Resourced by resourced {

    init {
        icon = Image(R.image.ic_launcher)
        title = getString(R.string.about)
        content = hbox {
            padding = Insets(48.0)
            imageView(Image(R.image.logo_launcher)) {
                fitWidth = 172.0
                fitHeight = 172.0
            }
            vbox {
                alignment = CENTER_LEFT
                textFlow {
                    text("Wijaya ") { font = getFont(R.font.lato_bold, 24) }
                    text("Printing") { font = getFont(R.font.lato_light, 24) }
                }
                text("${getString(R.string.version)} $VERSION") { font = getFont(R.font.lato_regular, 12) } marginTop 2
                text(getString(R.string.about_notice)) { font = getFont(R.font.lato_bold, 12) } marginTop 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") { font = getFont(R.font.lato_bold, 12) }
                    text("JavaFX") { font = getFont(R.font.lato_regular, 12) }
                } marginTop 4
                textFlow {
                    text("${getString(R.string.author)}  ") { font = getFont(R.font.lato_bold, 12) }
                    text("Hendra Anggrian") { font = getFont(R.font.lato_regular, 12) }
                } marginTop 4
                hbox {
                    button("GitHub") { setOnAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting")) } }
                    button(getString(R.string.check_for_updates)) { setOnAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting/releases")) } } marginLeft 8
                } marginTop 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        expandableContent = hbox {
            listView = kotfx.scene.listView {
                prefHeight = 256.0
                items = License.values().toObservableList()
                setCellFactory {
                    object : GraphicListCell<License>() {
                        override fun getGraphic(item: License): Node = kotfx.scene.vbox {
                            label(item.repo) { font = getFont(R.font.lato_regular, 12) }
                            label(item.owner) { font = getFont(R.font.lato_bold, 12) }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotfx.scene.textArea {
                prefHeight = 256.0
                isEditable = false
                textProperty().bind(stringBindingOf(listView.selectionModel.selectedIndexProperty()) {
                    listView.selectionModel.selectedItem?.content ?: getString(R.string.select_license)
                })
            }) { isCollapsible = false }
        }
        button(ButtonType("Homepage", CANCEL_CLOSE)).apply {
            visibleProperty().bind(dialogPane.expandedProperty() and booleanBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem != null })
            addEventFilter(ACTION) {
                it.consume()
                getDesktop().browse(URI(listView.selectionModel.selectedItem.homepage))
            }
        }
        button(CLOSE)
    }
}