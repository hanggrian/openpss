package com.hanggrian.openpss.ui.main.help

import com.hanggrian.openpss.BuildConfig
import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.STYLESHEET_OPENPSS
import com.hanggrian.openpss.ui.main.License
import com.jfoenix.controls.JFXButton
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Hyperlink
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
import ktfx.jfoenix.layouts.styledJfxButton
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
 * The only dialog not using [com.hanggrian.openpss.popup.dialog.Dialog].
 * This is because it uses native dialog's expandable content.
 */
class AboutDialog(context: Context) :
    Dialog<Unit>(),
    Context by context {
    private val licenseList =
        jfxListView<License> {
            items = License.entries.toObservableList()
            cellFactory {
                onUpdate { license, empty ->
                    if (license == null || empty) {
                        return@onUpdate
                    }
                    graphic =
                        vbox {
                            label(license.repo)
                            styledLabel(license.owner, null, R.style_bold)
                        }
                }
            }
            contextMenu {
                "Homepage" {
                    disableProperty().bind(
                        !this@jfxListView.selectionModel.selectedItemProperty().isNotNull,
                    )
                    onAction {
                        desktop?.browse(URI(this@jfxListView.selectionModel.selectedItem.homepage))
                    }
                }
            }
        }

    init {
        icon = Image(R.image_menu_about)
        title = getString(R.string_about)
        dialogPane.run {
            stylesheets += STYLESHEET_OPENPSS
            content =
                hbox {
                    padding = insetsOf(48)
                    imageView(R.image_logo)
                    vbox {
                        alignment = LEFT
                        textFlow {
                            styledText(
                                "${BuildConfig.FULL_NAME.substringBefore(' ')} ",
                                R.style_bold,
                                R.style_display2,
                            )
                            styledText(
                                BuildConfig.FULL_NAME.substringAfter(' '),
                                R.style_light,
                                R.style_display2,
                            )
                        }
                        text("${getString(R.string_version)} ${BuildConfig.VERSION}") {
                            font = Font.font(12.0)
                        }.margin(insetsOf(top = 2))
                        text(
                            getString(
                                R.string_built_with_open_source_software_expand_to_see_licenses,
                            ),
                        ).margin(insetsOf(top = 20))
                        textFlow {
                            "${getString(R.string_powered_by)} " { font = Font.font(12.0) }
                            styledText("JavaFX", R.style_bold)
                        }.margin(insetsOf(top = 4))
                        textFlow {
                            "${getString(R.string_author)} " { font = Font.font(12.0) }
                            styledText(BuildConfig.AUTHOR, R.style_bold)
                        }.margin(insetsOf(top = 4))
                        hbox {
                            spacing = getDouble(R.dimen_padding_medium)
                            styledJfxButton("GitHub", null, R.style_raised) {
                                buttonType = JFXButton.ButtonType.RAISED
                                onAction(Dispatchers.IO) {
                                    desktop?.browse(URI(BuildConfig.WEBSITE))
                                }
                            }
                            styledJfxButton("Email", null, R.style_flat) {
                                onAction(Dispatchers.IO) {
                                    desktop?.mail(URI("mailto:${BuildConfig.EMAIL}"))
                                }
                            }
                        }.margin(insetsOf(top = 20))
                    }.margin(insetsOf(top = 48))
                }
            expandableContent =
                masterDetailPane {
                    prefHeight = 200.0
                    dividerPosition = 0.3
                    showDetailNodeProperty()
                        .bind(licenseList.selectionModel.selectedItemProperty().isNotNull)
                    masterNode = licenseList
                    detailNode =
                        ktfx.jfoenix.layouts.jfxTextArea {
                            isEditable = false
                            licenseList.selectionModel
                                .selectedItemProperty()
                                .listener { _, _, license -> text = license?.getContent() }
                        }
                }
        }
        buttons.add(ButtonType.CLOSE)

        runLater {
            dialogPane.run {
                val detailsButton = find<Hyperlink>(".details-button")
                detailsButton.text = getString(R.string__open_source_license_show)
                expandedProperty().listener { _, _, isExpanded ->
                    detailsButton.text =
                        getString(
                            when {
                                isExpanded -> R.string__open_source_license_hide
                                else -> R.string__open_source_license_show
                            },
                        )
                }
            }
        }
    }
}
