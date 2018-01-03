package com.wijayaprinting.manager.dialog

import com.wijayaprinting.data.License
import com.wijayaprinting.data.WP
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.internal.Resourceful
import javafx.collections.ObservableList
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.Dialog
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.text.Font.loadFont
import kotfx.*
import java.awt.Desktop.getDesktop
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URI
import java.util.*
import java.util.stream.Collectors.joining


class AboutDialog(override val resources: ResourceBundle) : Dialog<Unit>(), Resourceful {

    private val latoBold = getResource(R.ttf.lato_bold).toExternalForm()
    private val latoLight = getResource(R.ttf.lato_light).toExternalForm()
    private val latoRegular = getResource(R.ttf.lato_regular).toExternalForm()

    init {
        title = getString(R.string.about)
        content = hbox {
            padding = Insets(48.0)
            imageView(Image(R.png.logo_launcher)) {
                fitWidth = 172.0
                fitHeight = 172.0
            }
            vbox {
                alignment = CENTER_LEFT
                textFlow {
                    text("Wijaya Printing ") { font = loadFont(latoBold, 24.0) }
                    text("Manager") { font = loadFont(latoLight, 24.0) }
                }
                text("${getString(R.string.version)} ${com.wijayaprinting.manager.BuildConfig.VERSION}") { font = loadFont(latoRegular, 12.0) } marginTop 2
                text(getString(R.string.about_notice)) { font = loadFont(latoBold, 12.0) } marginTop 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") { font = loadFont(latoBold, 12.0) }
                    text("JavaFX") { font = loadFont(latoRegular, 12.0) }
                } marginTop 4
                textFlow {
                    text("Data  ") { font = loadFont(latoBold, 12.0) }
                    text("${getString(R.string.version)} ${com.wijayaprinting.data.BuildConfig.VERSION}") { font = loadFont(latoRegular, 12.0) }
                } marginTop 4
                textFlow {
                    text("${getString(R.string.author)}  ") { font = loadFont(latoBold, 12.0) }
                    text("Hendra Anggrian") { font = loadFont(latoRegular, 12.0) }
                } marginTop 4
                hbox {
                    button("GitHub") { setOnAction { getDesktop().browse(URI("https://github.com/WijayaPrinting")) } }
                    button(getString(R.string.check_for_updates)) { setOnAction { getDesktop().browse(URI("https://github.com/WijayaPrinting/manager/releases")) } } marginLeft 8
                } marginTop 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        expandableContent = hbox {
            listView = kotfx.listView {
                prefHeight = 256.0
                items = licenses
                setCellFactory {
                    object : ListCell<License>() {
                        override fun updateItem(item: License?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = null
                            graphic = null
                            if (item != null && !empty) graphic = kotfx.vbox {
                                label(item.name) { font = loadFont(latoRegular, 12.0) }
                                label(item.owner) { font = loadFont(latoBold, 12.0) }
                            }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotfx.textArea {
                prefHeight = 256.0
                isEditable = false
                textProperty() bind stringBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem?.getContent(this@AboutDialog) ?: getString(R.string.select_license) }
            }) { isCollapsible = false }
        }
        button(ButtonType("Homepage", CANCEL_CLOSE)).apply {
            visibleProperty() bind (dialogPane.expandedProperty() and booleanBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem != null })
            addEventFilter(ACTION) { event ->
                event.consume()
                getDesktop().browse(URI(listView.selectionModel.selectedItem.homepage))
            }
        }
        button(CLOSE)
    }

    private val licenses
        get(): ObservableList<License> = WP.licenses.addAll(
                License("Apache", "POI OOXML", "https://poi.apache.org"),
                License("Apache", "Commons Lang", "https://commons.apache.org/lang"),
                License("Apache", "Commons Math", "https://commons.apache.org/math"),
                License("Apache", "Commons Validator", "https://commons.apache.org/validator"),
                License("Google", "Guava", "https://github.com/google/guava"),
                License("Hendra Anggrian", "Kotfx", "https://github.com/hendraanggrian/kotfx"),
                License("ReactiveX", "RxJavaFX", "https://github.com/ReactiveX/RxJavaFX"),
                License("Slf4j", "Log4j12", "https://www.slf4j.org")).toObservableList()

    private fun License.getContent(resourceful: Resourceful): String = File(resourceful.getResource("/${owner.shorten}_${name.shorten}.txt").toURI())
            .inputStream()
            .use { return BufferedReader(InputStreamReader(it)).lines().collect(joining("\n")) }

    private val String.shorten: String
        get() = toLowerCase()
                .replace(' ', '_')
                .replace('/', '_')
}