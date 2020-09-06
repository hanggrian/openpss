package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.content.STYLESHEET_OPENPSS
import com.hendraanggrian.openpss.ui.main.License
import com.jfoenix.controls.JFXButton
import javafx.scene.control.Dialog
import javafx.scene.control.Hyperlink
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.text.Font
import kotlinx.coroutines.Dispatchers
import ktfx.cells.cellFactory
import ktfx.collections.toObservableList
import ktfx.controls.LEFT
import ktfx.controls.find
import ktfx.controls.insetsOf
import ktfx.controlsfx.layouts.masterDetailPane
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.dialogs.buttons
import ktfx.dialogs.icon
import ktfx.jfoenix.layouts.jfxListView
import ktfx.jfoenix.layouts.styledJFXButton
import ktfx.layouts.contextMenu
import ktfx.layouts.hbox
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.styledLabel
import ktfx.layouts.styledText
import ktfx.layouts.text
import ktfx.layouts.textFlow
import ktfx.layouts.vbox
import ktfx.runLater
import java.net.URI

/**
 * The only dialog not using [com.hendraanggrian.openpss.popup.dialog.Dialog].
 * This is because it uses native dialog's expandable content.
 */
class AboutDialog(context: Context) : Dialog<Unit>(), Context by context {

    private val licenseList: ListView<License> = jfxListView {
        items = License.values().toObservableList()
        cellFactory {
            onUpdate { license, empty ->
                if (license != null && !empty) graphic = vbox {
                    label(license.repo)
                    styledLabel(license.owner, null, R.style.bold)
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
            stylesheets += STYLESHEET_OPENPSS
            content = hbox {
                padding = insetsOf(48)
                imageView(R.image.logo)
                vbox {
                    alignment = LEFT
                    textFlow {
                        styledText("${BuildConfig.FULL_NAME.substringBefore(' ')} ", R.style.bold, R.style.display2)
                        styledText(BuildConfig.FULL_NAME.substringAfter(' '), R.style.light, R.style.display2)
                    }
                    text("${getString(R.string.version)} ${BuildConfig.VERSION}") {
                        font = Font.font(12.0)
                    }.margin(insetsOf(top = 2))
                    text(getString(R.string.built_with_open_source_software_expand_to_see_licenses)).margin(insetsOf(top = 20))
                    textFlow {
                        "${getString(R.string.powered_by)} " { font = Font.font(12.0) }
                        styledText("JavaFX", R.style.bold)
                    }.margin(insetsOf(top = 4))
                    textFlow {
                        "${getString(R.string.author)} " { font = Font.font(12.0) }
                        styledText(BuildConfig.AUTHOR, R.style.bold)
                    }.margin(insetsOf(top = 4))
                    hbox {
                        spacing = getDouble(R.dimen.padding_medium)
                        styledJFXButton("GitHub", null, R.style.raised) {
                            buttonType = JFXButton.ButtonType.RAISED
                            onAction(Dispatchers.IO) { desktop?.browse(URI(BuildConfig.WEBSITE)) }
                        }
                        styledJFXButton("Email", null, R.style.flat) {
                            onAction(Dispatchers.IO) { desktop?.mail(URI("mailto:${BuildConfig.EMAIL}")) }
                        }
                    }.margin(insetsOf(top = 20))
                }.margin(insetsOf(top = 48))
            }
            expandableContent = masterDetailPane {
                prefHeight = 200.0
                dividerPosition = 0.3
                showDetailNodeProperty().bind(licenseList.selectionModel.selectedItemProperty().isNotNull)
                masterNode = licenseList
                detailNode = ktfx.jfoenix.layouts.jfxTextArea {
                    isEditable = false
                    licenseList.selectionModel.selectedItemProperty().listener { _, _, license ->
                        text = license?.getContent()
                    }
                }
            }
        }
        buttons.close()

        runLater {
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
