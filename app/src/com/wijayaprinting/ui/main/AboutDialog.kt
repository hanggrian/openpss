package com.wijayaprinting.ui.main

import com.wijayaprinting.BuildConfig.VERSION
import com.wijayaprinting.R
import com.wijayaprinting.scene.control.GraphicListCell
import com.wijayaprinting.ui.Resourced
import com.wijayaprinting.util.getExternalForm
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.Node
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.image.Image
import kotfx.bindings.and
import kotfx.bindings.booleanBindingOf
import kotfx.bindings.stringBindingOf
import kotfx.collections.toObservableList
import kotfx.coroutines.cellFactory
import kotfx.dialogs.addButton
import kotfx.dialogs.content
import kotfx.dialogs.expandableContent
import kotfx.dialogs.icon
import kotfx.layout.button
import kotfx.layout.hbox
import kotfx.layout.imageView
import kotfx.layout.label
import kotfx.layout.text
import kotfx.layout.textFlow
import kotfx.layout.titledPane
import kotfx.layout.vbox
import kotfx.loadFont
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
                    text("Wijaya ") { loadFont(getExternalForm(R.font.lato_bold), 24.0) }
                    text("Printing") { loadFont(getExternalForm(R.font.lato_light), 24.0) }
                }
                text("${getString(R.string.version)} $VERSION") { loadFont(getExternalForm(R.font.lato_regular), 12.0) } marginTop 2
                text(getString(R.string.about_notice)) { loadFont(getExternalForm(R.font.lato_bold), 12.0) } marginTop 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") { loadFont(getExternalForm(R.font.lato_bold), 12.0) }
                    text("JavaFX") { loadFont(getExternalForm(R.font.lato_regular), 12.0) }
                } marginTop 4
                textFlow {
                    text("${getString(R.string.author)}  ") { loadFont(getExternalForm(R.font.lato_bold), 12.0) }
                    text("Hendra Anggrian") { loadFont(getExternalForm(R.font.lato_regular), 12.0) }
                } marginTop 4
                hbox {
                    button("GitHub") { setOnAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting")) } }
                    button(getString(R.string.check_for_updates)) { setOnAction { getDesktop().browse(URI("https://github.com/hendraanggrian/wijayaprinting/releases")) } } marginLeft 8
                } marginTop 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        expandableContent = hbox {
            listView = kotfx.layout.listView {
                prefHeight = 256.0
                items = License.values().toObservableList()
                cellFactory {
                    object : GraphicListCell<License>() {
                        override fun getGraphic(item: License): Node = kotfx.layout.vbox {
                            label(item.repo) { loadFont(getExternalForm(R.font.lato_regular), 12.0) }
                            label(item.owner) { loadFont(getExternalForm(R.font.lato_bold), 12.0) }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotfx.layout.textArea {
                prefHeight = 256.0
                isEditable = false
                textProperty().bind(stringBindingOf(listView.selectionModel.selectedIndexProperty()) {
                    listView.selectionModel.selectedItem?.content ?: getString(R.string.select_license)
                })
            }) { isCollapsible = false }
        }
        addButton("Homepage", CANCEL_CLOSE).apply {
            visibleProperty().bind(dialogPane.expandedProperty() and booleanBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem != null })
            addEventFilter(ACTION) {
                it.consume()
                getDesktop().browse(URI(listView.selectionModel.selectedItem.homepage))
            }
        }
        addButton(CLOSE)
    }
}