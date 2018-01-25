package com.wijayaprinting.ui

import com.wijayaprinting.BuildConfig
import com.wijayaprinting.R
import com.wijayaprinting.util.getFont
import com.wijayaprinting.util.getResourceAsStream
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
import kotfx.*
import java.awt.Desktop.getDesktop
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.util.stream.Collectors.joining

class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Resourced by resourced {

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
                    text("Wijaya ") { font = getFont(R.ttf.lato_bold, 24) }
                    text("Printing") { font = getFont(R.ttf.lato_light, 24) }
                }
                text("${getString(R.string.version)} ${BuildConfig.VERSION}") { font = getFont(R.ttf.lato_regular, 12) } marginTop 2
                text(getString(R.string.about_notice)) { font = getFont(R.ttf.lato_bold, 12) } marginTop 20
                textFlow {
                    text("${getString(R.string.powered_by)}  ") { font = getFont(R.ttf.lato_bold, 12) }
                    text("JavaFX, MongoDB") { font = getFont(R.ttf.lato_regular, 12) }
                } marginTop 4
                textFlow {
                    text("${getString(R.string.author)}  ") { font = getFont(R.ttf.lato_bold, 12) }
                    text("Hendra Anggrian") { font = getFont(R.ttf.lato_regular, 12) }
                } marginTop 4
                hbox {
                    button("GitHub") { setOnAction { browse("https://github.com/hendraanggrian/wijayaprinting") } }
                    button(getString(R.string.check_for_updates)) { setOnAction { browse("https://github.com/hendraanggrian/wijayaprinting") } } marginLeft 8
                } marginTop 20
            } marginLeft 48
        }
        lateinit var listView: ListView<License>
        expandableContent = hbox {
            listView = kotfx.listView {
                prefHeight = 256.0
                items = License.values().toObservableList()
                setCellFactory {
                    object : ListCell<License>() {
                        override fun updateItem(item: License?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = null
                            graphic = null
                            if (item != null && !empty) graphic = kotfx.vbox {
                                label(item.repo) { font = getFont(R.ttf.lato_regular, 12) }
                                label(item.owner) { font = getFont(R.ttf.lato_bold, 12) }
                            }
                        }
                    }
                }
            }
            titledPane(getString(R.string.open_source_software), listView) { isCollapsible = false }
            titledPane(getString(R.string.license), kotfx.textArea {
                prefHeight = 256.0
                isEditable = false
                textProperty() bind stringBindingOf(listView.selectionModel.selectedIndexProperty()) {
                    listView.selectionModel.selectedItem?.content ?: getString(R.string.select_license)
                }
            }) { isCollapsible = false }
        }
        button(ButtonType("Homepage", CANCEL_CLOSE)).apply {
            visibleProperty() bind (dialogPane.expandedProperty() and booleanBindingOf(listView.selectionModel.selectedIndexProperty()) { listView.selectionModel.selectedItem != null })
            addEventFilter(ACTION) {
                it.consume()
                browse(listView.selectionModel.selectedItem.homepage)
            }
        }
        button(CLOSE)
    }

    private fun browse(url: String) = try {
        getDesktop().browse(URI(url))
    } catch (e: Exception) {
        errorAlert(e.message.toString()).showAndWait()
    }

    enum class License(val owner: String, val repo: String, val homepage: String) {
        APACHE_COMMONS_LANG("Apache", "commons-lang", "https://commons.apache.org/lang"),
        APACHE_COMMONS_MATH("Apache", "commons-math", "https://commons.apache.org/math"),
        APACHE_COMMONS_VALIDATOR("Apache", "commons-validator", "https://commons.apache.org/validator"),
        APACHE_POI("Apache", "POI", "https://poi.apache.org"),
        GOOGLE_GUAVA("Google", "Guava", "https://github.com/google/guava"),
        HENDRAANGGRIAN_KOTFX("Hendra Anggrian", "kotfx", "https://github.com/hendraanggrian/kotfx"),
        JETBRAINS_KOTLIN("JetBrains", "Kotlin", "http://kotlinlang.org"),
        JODAORG_JODA_TIME("JodaOrg", "Joda-Time", "www.joda.org/joda-time"),
        REACTIVEX_RXJAVAFX("ReactiveX", "RxJavaFX", "https://github.com/ReactiveX/RxJavaFX"),
        REACTIVEX_RXKOTLIN("ReactiveX", "RxKotlin", "https://github.com/ReactiveX/RxKotlin"),
        SLF4J_LOG4J12("Slf4j", "Log4j12", "https://www.slf4j.org");

        val content: String
            get() = getResourceAsStream("/${name.toLowerCase()}.txt").use {
                return BufferedReader(InputStreamReader(it)).lines().collect(joining("\n"))
            }
    }
}