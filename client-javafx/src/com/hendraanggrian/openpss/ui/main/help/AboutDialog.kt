package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.ui.FxComponent
import com.hendraanggrian.openpss.ui.Stylesheets
import com.hendraanggrian.openpss.ui.main.License
import com.jfoenix.controls.JFXButton
import javafx.geometry.Pos
import javafx.scene.control.Dialog
import javafx.scene.control.Hyperlink
import javafx.scene.control.ListView
import javafx.scene.image.Image
import ktfx.collections.toObservableList
import ktfx.controls.find
import ktfx.controls.paddingAll
import ktfx.controlsfx.masterDetailPane
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.dialogs.closeButton
import ktfx.dialogs.icon
import ktfx.jfoenix.jfxButton
import ktfx.jfoenix.jfxListView
import ktfx.later
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.text.updateFont
import java.net.URI

/**
 * The only dialog not using [com.hendraanggrian.openpss.popup.dialog.Dialog].
 * This is because it uses native dialog's expandable content.
 */
class AboutDialog(component: FxComponent) : Dialog<Unit>(), FxComponent by component {

    private val licenseList: ListView<License> = jfxListView {
        items = License.values().toObservableList()
        cellFactory {
            onUpdate { license, empty ->
                if (license != null && !empty) graphic = ktfx.layouts.vbox {
                    label(license.repo)
                    label(license.owner) { styleClass += R.style.bold }
                }
            }
        }
        contextMenu {
            "Homepage" {
                disableProperty().bind(!this@jfxListView.selectionModel.selectedItemProperty().isNotNull)
                onAction { desktop?.browse(URI(this@jfxListView.selectionModel.selectedItem.homepage)) }
            }
        }
    }

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        dialogPane.run {
            stylesheets += Stylesheets.OPENPSS
            content = hbox {
                paddingAll = 48
                imageView(R.image.logo)
                vbox {
                    alignment = Pos.CENTER_LEFT
                    textFlow {
                        "${BuildConfig.FULL_NAME.substringBefore(' ')} " {
                            styleClass.addAll(R.style.bold, R.style.display2)
                        }
                        (BuildConfig.FULL_NAME.substringAfter(' ')) {
                            styleClass.addAll(R.style.light, R.style.display2)
                        }
                    }
                    text("${getString(R.string.version)} ${BuildConfig.VERSION}") {
                        updateFont(12)
                    } marginTop 2
                    text(getString(R.string.built_with_open_source_software_expand_to_see_licenses)) marginTop 20
                    textFlow {
                        "${getString(R.string.powered_by)} " {
                            updateFont(12)
                        }
                        "JavaFX" { styleClass += R.style.bold }
                    } marginTop 4
                    textFlow {
                        "${getString(R.string.author)} " {
                            updateFont(12)
                        }
                        BuildConfig.USER {
                            styleClass += R.style.bold
                        }
                    } marginTop 4
                    hbox {
                        spacing = getDouble(R.value.padding_medium)
                        jfxButton("GitHub") {
                            styleClass += R.style.raised
                            buttonType = JFXButton.ButtonType.RAISED
                            onAction { desktop?.browse(URI(BuildConfig.WEBSITE)) }
                        }
                        jfxButton("Email") {
                            styleClass += R.style.flat
                            onAction { desktop?.mail(URI("mailto:${BuildConfig.EMAIL}")) }
                        }
                    } marginTop 20
                } marginLeft 48
            }
            expandableContent = masterDetailPane {
                prefHeight = 200.0
                dividerPosition = 0.3
                showDetailNodeProperty().bind(licenseList.selectionModel.selectedItemProperty().isNotNull)
                masterNode = licenseList
                detailNode = ktfx.jfoenix.jfxTextArea {
                    isEditable = false
                    licenseList.selectionModel.selectedItemProperty().listener { _, _, license ->
                        text = license?.getContent()
                    }
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