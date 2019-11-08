package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.BuildConfig2
import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.ui.Stylesheets
import com.hendraanggrian.openpss.ui.main.License
import com.jfoenix.controls.JFXButton
import java.net.URI
import javafx.geometry.Pos
import javafx.scene.control.Dialog
import javafx.scene.control.Hyperlink
import javafx.scene.control.ListView
import javafx.scene.image.Image
import ktfx.cells.cellFactory
import ktfx.collections.toObservableList
import ktfx.controls.find
import ktfx.controls.selectedBinding
import ktfx.controlsfx.layouts.masterDetailPane
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.dialogs.buttons
import ktfx.dialogs.icon
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxListView
import ktfx.jfoenix.layouts.jfxTextArea
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.paddingAll
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.runLater
import ktfx.text.pt

/**
 * The only dialog not using [com.hendraanggrian.openpss.popup.dialog.Dialog].
 * This is because it uses native dialog's expandable content.
 */
class AboutDialog(component: FxComponent) : Dialog<Unit>(), FxComponent by component {

    private val licenseList: ListView<License> = jfxListView {
        items = License.values().toObservableList()
        cellFactory {
            onUpdate { license, empty ->
                if (license != null && !empty) graphic = vbox {
                    label(license.repo)
                    label(license.owner) { styleClass += R.style.bold }
                }
            }
        }
        contextMenu {
            "Homepage" {
                disableProperty().bind(!this@jfxListView.selectionModel.selectedBinding)
                onAction { desktop?.browse(URI(this@jfxListView.selectionModel.selectedItem.homepage)) }
            }
        }
    }

    init {
        icon = Image(R.image.menu_about)
        title = getString(R2.string.about)
        dialogPane.run {
            stylesheets += Stylesheets.OPENPSS
            content = hbox {
                paddingAll = 48.0
                imageView(R.image.logo)
                vbox {
                    marginLeft = 48.0
                    alignment = Pos.CENTER_LEFT
                    textFlow {
                        "${BuildConfig2.FULL_NAME.substringBefore(' ')} " {
                            styleClass.addAll(R.style.bold, R.style.display2)
                        }
                        (BuildConfig2.FULL_NAME.substringAfter(' ')) {
                            styleClass.addAll(R.style.light, R.style.display2)
                        }
                    }
                    text("${getString(R2.string.version)} ${BuildConfig2.VERSION}") {
                        font = 12.pt
                        marginTop = 2.0
                    }
                    text(getString(R2.string.built_with_open_source_software_expand_to_see_licenses)) {
                        marginTop = 20.0
                    }
                    textFlow {
                        marginTop = 4.0
                        "${getString(R2.string.powered_by)} " {
                            font = 12.pt
                        }
                        "JavaFX" { styleClass += R.style.bold }
                    }
                    textFlow {
                        marginTop = 4.0
                        "${getString(R2.string.author)} " {
                            font = 12.pt
                        }
                        BuildConfig2.USER {
                            styleClass += R.style.bold
                        }
                    }
                    hbox {
                        marginTop = 20.0
                        spacing = getDouble(R.value.padding_medium)
                        jfxButton("GitHub") {
                            styleClass += R.style.raised
                            buttonType = JFXButton.ButtonType.RAISED
                            onAction { desktop?.browse(URI(BuildConfig2.WEBSITE)) }
                        }
                        jfxButton("Email") {
                            styleClass += R.style.flat
                            onAction { desktop?.mail(URI("mailto:${BuildConfig2.EMAIL}")) }
                        }
                    }
                }
            }
            expandableContent = masterDetailPane {
                prefHeight = 200.0
                dividerPosition = 0.3
                showDetailNodeProperty().bind(licenseList.selectionModel.selectedBinding)
                addNode(licenseList)
                jfxTextArea {
                    isEditable = false
                    licenseList.selectionModel.selectedItemProperty().listener { _, _, license ->
                        text = license?.getContent()
                    }
                }
            }
        }
        buttons {
            close()
        }
        runLater {
            dialogPane.run {
                val detailsButton = find<Hyperlink>(".details-button")
                detailsButton.text = getString(R2.string._open_source_license_show)
                expandedProperty().listener { _, _, isExpanded ->
                    detailsButton.text = getString(
                        when {
                            isExpanded -> R2.string._open_source_license_hide
                            else -> R2.string._open_source_license_show
                        }
                    )
                }
            }
        }
    }
}
