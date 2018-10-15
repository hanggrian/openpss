package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig.AUTHOR
import com.hendraanggrian.openpss.BuildConfig.EMAIL
import com.hendraanggrian.openpss.BuildConfig.FULL_NAME
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.BuildConfig.WEBSITE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.main.License
import com.hendraanggrian.openpss.util.desktop
import com.hendraanggrian.openpss.util.getFont
import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.control.SelectionModel
import javafx.scene.image.Image
import javafx.scene.text.Font.font
import ktfx.collections.toObservableList
import ktfx.controlsfx.masterDetailPane
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.titledPane
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.scene.control.closeButton
import ktfx.scene.control.icon
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize
import java.net.URI

class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Selectable<License>, Resourced by resourced {

    private val licenseList: ListView<License> = ktfx.layouts.listView(License.values().toObservableList()) {
        cellFactory {
            onUpdate { license, empty ->
                if (license != null && !empty) graphic = ktfx.layouts.vbox {
                    label(license.repo) { fontSize = 12.0 }
                    label(license.owner) { font = bold(12) }
                }
            }
        }
        contextMenu {
            "Homepage" {
                disableProperty().bind(!this@listView.selectionModel.selectedItemProperty().isNotNull)
                onAction { desktop?.browse(URI(selected!!.homepage)) }
            }
        }
    }

    override val selectionModel: SelectionModel<License> get() = licenseList.selectionModel

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        hbox {
            paddingAll = 48.0
            imageView(R.image.display_launcher) {
                fitHeight = 172.0
                fitWidth = 172.0
            }
            vbox {
                alignment = CENTER_LEFT
                textFlow {
                    "${FULL_NAME.substringBefore(' ')} " { font = bold(24) }
                    (FULL_NAME.substringAfter(' ')) { font = getFont(R.font.sf_pro_text_light, 24) }
                }
                text("${getString(R.string.version)} $VERSION") { font = font(12.0) } marginTop 2.0
                text(getString(R.string.built_with_open_source_software_expand_to_see_licenses)) marginTop 20.0
                textFlow {
                    "${getString(R.string.powered_by)} " { font = font(12.0) }
                    "JavaFX" { font = bold(12) }
                } marginTop 4.0
                textFlow {
                    "${getString(R.string.author)} " { font = font(12.0) }
                    AUTHOR { font = bold(12) }
                } marginTop 4.0
                hbox {
                    spacing = R.dimen.padding_medium.toDouble()
                    jfxButton("GitHub") {
                        styleClass += App.STYLE_BUTTON_RAISED
                        buttonType = JFXButton.ButtonType.RAISED
                        onAction { desktop?.browse(URI(WEBSITE)) }
                    }
                    jfxButton("Email") {
                        styleClass += App.STYLE_BUTTON_FLAT
                        onAction { desktop?.mail(URI("mailto:$EMAIL")) }
                    }
                } marginTop 20.0
            } marginLeft 48.0
        }
        titledPane(getString(R.string.open_source_software_license)) {
            isCollapsible = false
            masterDetailPane {
                maxHeight = 256.0
                dividerPosition = 0.3
                showDetailNodeProperty().bind(selectedBinding)
                masterNode = licenseList
                detailNode = ktfx.layouts.textArea {
                    isEditable = false
                    selectedProperty.listener { _, _, license -> text = license?.getContent() }
                }
            }
        }
        closeButton()
    }
}