package com.wijayaprinting.manager.dialog

import com.wijayaprinting.manager.App
import com.wijayaprinting.manager.R
import com.wijayaprinting.manager.internal.Resourceful
import javafx.event.ActionEvent.ACTION
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType
import javafx.scene.control.ButtonType.CLOSE
import javafx.scene.control.Dialog
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

    private val latoBold = App::class.java.getResource(R.ttf.lato_bold).toExternalForm()
    private val latoLight = App::class.java.getResource(R.ttf.lato_light).toExternalForm()
    private val latoRegular = App::class.java.getResource(R.ttf.lato_regular).toExternalForm()

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
                text(getString(R.string.about_info)) { font = loadFont(latoBold, 12.0) } marginTop 20
                textFlow {
                    text("Data  ") { font = loadFont(latoBold, 12.0) }
                    text("${getString(R.string.version)} ${com.wijayaprinting.data.BuildConfig.VERSION}") { font = loadFont(latoRegular, 12.0) }
                } marginTop 4
                textFlow {
                    text("${getString(R.string.author)}  ") { font = loadFont(latoBold, 12.0) }
                    text("Hendra Anggrian") { font = loadFont(latoRegular, 12.0) }
                } marginTop 4
                hbox {
                    button("GitHub") { setOnAction { getDesktop().browse(URI("https://github.com/WijayaPrinting/")) } }
                    button(getString(R.string.check_for_updates)) { setOnAction { getDesktop().browse(URI("https://github.com/WijayaPrinting/manager/releases/")) } } marginLeft 8
                } marginTop 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        expandableContent = hbox {
            listView = kotfx.listView {
                prefHeight = 256.0
                items = License.listAll()
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotfx.textArea {
                prefHeight = 256.0
                isEditable = false
                textProperty() bind stringBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem?.content ?: getString(R.string.select_license) }
            }) { isCollapsible = false }
        }
        addButton(ButtonType("Homepage", CANCEL_CLOSE)).apply {
            visibleProperty() bind booleanBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem != null }
            addEventFilter(ACTION) { event ->
                event.consume()
                getDesktop().browse(URI(listView.selectionModel.selectedItem.homepage))
            }
        }
        addButton(CLOSE)
    }

    data class License(
            private val name: String,
            private val resource: String,
            val homepage: String
    ) {
        override fun toString(): String = name

        val content: String get() = File(App::class.java.getResource(resource).toURI()).inputStream().use { return BufferedReader(InputStreamReader(it)).lines().collect(joining("\n")) }

        companion object {
            fun listAll() = observableListOf(
                    License("Apache POI", R.txt.license_apache_poi, "https://poi.apache.org/"),
                    License("Apache Commons Lang", R.txt.license_apache_commonslang, "https://commons.apache.org/lang/"),
                    License("Apache Commons Math", R.txt.license_apache_commonsmath, "https://commons.apache.org/math/"),
                    License("Apache Commons Validator", R.txt.license_apache_commonsvalidator, "https://commons.apache.org/validator/"),
                    License("Guava", R.txt.license_guava, "https://github.com/google/guava/"),
                    License("Kotfx", R.txt.license_kotfx, "https://github.com/hendraanggrian/kotfx/"),
                    License("Kotlin", R.txt.license_kotlin, "https://kotlinlang.org/"),
                    License("RxJavaFX", R.txt.license_rxjavafx, "https://github.com/ReactiveX/RxJavaFX/"),
                    License("SLF4J", R.txt.license_slf4j, "https://www.slf4j.org/")
            )
        }
    }
}