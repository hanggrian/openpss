package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.main.License
import com.hendraanggrian.openpss.util.desktop
import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos
import javafx.scene.control.Dialog
import javafx.scene.control.Hyperlink
import javafx.scene.control.ListView
import javafx.scene.control.SelectionModel
import javafx.scene.image.Image
import javafx.scene.text.Font
import ktfx.application.later
import ktfx.collections.toObservableList
import ktfx.controlsfx.masterDetailPane
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxListView
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.scene.control.closeButton
import ktfx.scene.control.icon
import ktfx.scene.find
import ktfx.scene.layout.paddingAll
import ktfx.scene.text.fontSize
import java.net.URI

/**
 * The only dialog not using [com.hendraanggrian.openpss.popup.dialog.Dialog].
 * This is because it uses native dialog's expandable content.
 */
class AboutDialog(resourced: Resourced) : Dialog<Unit>(), Selectable<License>, Resourced by resourced {

    private val licenseList: ListView<License> = jfxListView {
        items = License.values().toObservableList()
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
                disableProperty().bind(!this@jfxListView.selectionModel.selectedItemProperty().isNotNull)
                onAction { desktop?.browse(URI(selected!!.homepage)) }
            }
        }
    }

    override val selectionModel: SelectionModel<License> get() = licenseList.selectionModel

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        dialogPane.run {
            stylesheets += com.hendraanggrian.openpss.util.getStyle(R.style.openpss)
            content = hbox {
                paddingAll = 48.0
                imageView(R.image.logo)
                vbox {
                    alignment = Pos.CENTER_LEFT
                    textFlow {
                        "${BuildConfig.FULL_NAME.substringBefore(' ')} " { font = bold(24) }
                        (BuildConfig.FULL_NAME.substringAfter(' ')) {
                            font = com.hendraanggrian.openpss.util.getFont(R.font.sf_pro_text_light, 24)
                        }
                    }
                    text("${getString(R.string.version)} ${BuildConfig.VERSION}") {
                        font = Font.font(12.0)
                    } marginTop 2.0
                    text(getString(R.string.built_with_open_source_software_expand_to_see_licenses)) marginTop 20.0
                    textFlow {
                        "${getString(R.string.powered_by)} " { font = Font.font(12.0) }
                        "JavaFX" { font = bold(12) }
                    } marginTop 4.0
                    textFlow {
                        "${getString(R.string.author)} " { font = Font.font(12.0) }
                        BuildConfig.AUTHOR { font = bold(12) }
                    } marginTop 4.0
                    hbox {
                        spacing = R.dimen.padding_medium.toDouble()
                        jfxButton("GitHub") {
                            styleClass += App.STYLE_BUTTON_RAISED
                            buttonType = JFXButton.ButtonType.RAISED
                            onAction { desktop?.browse(URI(BuildConfig.WEBSITE)) }
                        }
                        jfxButton("Email") {
                            styleClass += App.STYLE_BUTTON_FLAT
                            onAction { desktop?.mail(URI("mailto:${BuildConfig.EMAIL}")) }
                        }
                    } marginTop 20.0
                } marginLeft 48.0
            }
            expandableContent = masterDetailPane {
                maxHeight = 256.0
                dividerPosition = 0.3
                showDetailNodeProperty().bind(selectedBinding)
                masterNode = licenseList
                detailNode = ktfx.jfoenix.jfxTextArea {
                    isEditable = false
                    selectedProperty.listener { _, _, license -> text = license?.getContent() }
                }
            }
        }
        closeButton()

        later {
            dialogPane.run {
                val detailsButton = find<Hyperlink>(".details-button")
                detailsButton.text = getString(R.string._open_source_license_show)
                expandedProperty().listener { _, _, isExpanded ->
                    detailsButton.text = getString(
                        when {
                            isExpanded -> R.string._open_source_license_hide
                            else -> R.string._open_source_license_show
                        }
                    )
                }
            }
        }
    }
}