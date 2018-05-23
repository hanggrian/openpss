package com.hendraanggrian.openpss.ui.main

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.FULL_NAME
import com.hendraanggrian.openpss.BuildConfig.USER
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.BuildConfig.WEBSITE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.Dialog
import com.hendraanggrian.openpss.localization.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.main.github.GitHubApi
import com.hendraanggrian.openpss.util.browseUrl
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.util.onActionFilter
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ButtonType.CANCEL
import javafx.scene.control.ListView
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.SelectionModel
import javafx.scene.image.Image
import javafx.scene.text.Font.font
import kotlinx.coroutines.experimental.launch
import ktfx.application.later
import ktfx.beans.value.and
import ktfx.collections.toObservableList
import ktfx.coroutines.FX
import ktfx.coroutines.listener
import ktfx.coroutines.onAction
import ktfx.layouts.button
import ktfx.layouts.hbox
import ktfx.layouts.hyperlink
import ktfx.layouts.imageView
import ktfx.layouts.label
import ktfx.layouts.listView
import ktfx.layouts.progressIndicator
import ktfx.layouts.text
import ktfx.layouts.textArea
import ktfx.layouts.textFlow
import ktfx.layouts.titledPane
import ktfx.layouts.vbox
import ktfx.listeners.cellFactory
import ktfx.scene.control.closeButton
import ktfx.scene.control.customButton
import ktfx.scene.control.errorAlert
import ktfx.scene.control.icon
import ktfx.scene.control.styledInfoAlert
import ktfx.scene.layout.maxSize
import ktfx.scene.layout.paddingAll
import java.util.concurrent.TimeUnit.SECONDS

class AboutDialog(resourced: Resourced) : Dialog<Nothing>(resourced), Selectable<License> {

    private lateinit var checkUpdateButton: Button
    private lateinit var checkUpdateProgress: ProgressIndicator
    private lateinit var licenseList: ListView<License>

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = hbox {
                paddingAll = 48.0
                imageView(Image(R.image.display_launcher))
                vbox {
                    alignment = CENTER_LEFT
                    textFlow {
                        "${FULL_NAME.substringBefore(' ')} " { font = getFont(R.font.sf_pro_text_bold, 24) }
                        (FULL_NAME.substringAfter(' ')) { font = getFont(R.font.sf_pro_text_light, 24) }
                    }
                    text("${getString(R.string.version)} $VERSION") { font = font(12.0) } marginTop 2.0
                    text(getString(R.string.built_with_open_source_software_expand_to_see_licenses)) marginTop 20.0
                    textFlow {
                        "${getString(R.string.powered_by)} " { font = font(12.0) }
                        "JavaFX" { font = getFont(R.font.sf_pro_text_bold, 12) }
                    } marginTop 4.0
                    textFlow {
                        "${getString(R.string.author)} " { font = font(12.0) }
                        USER { font = getFont(R.font.sf_pro_text_bold, 12) }
                    } marginTop 4.0
                    hbox {
                        button("GitHub") { onAction { browseUrl(WEBSITE) } }
                        checkUpdateButton = button(getString(R.string.check_for_updates)) {
                            onAction {
                                isDisable = true
                                checkUpdateProgress.isVisible = true
                                launch {
                                    try {
                                        val release = GitHubApi.create().getLatestRelease().get(10, SECONDS)
                                        launch(FX) {
                                            when {
                                                release.isNewer() -> styledInfoAlert(
                                                    getStyle(R.style.openpss),
                                                    title = getString(R.string.openpss_is_available, release.name),
                                                    buttonTypes = *arrayOf(CANCEL)
                                                ) {
                                                    dialogPane.content = ktfx.layouts.vbox {
                                                        release.assets.forEach { asset ->
                                                            hyperlink(asset.name) {
                                                                onAction {
                                                                    browseUrl(asset.downloadUrl)
                                                                    close()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }.show()
                                                else -> styledInfoAlert(
                                                    getStyle(R.style.openpss),
                                                    getString(R.string.you_re_up_to_date),
                                                    contentText = getString(
                                                        R.string.openpss_is_currently_the_newest_version_available,
                                                        VERSION)
                                                ).show()
                                            }
                                            isDisable = false
                                            checkUpdateProgress.isVisible = false
                                        }
                                    } catch (e: Exception) {
                                        if (DEBUG) e.printStackTrace()
                                        launch(FX) {
                                            errorAlert(getString(R.string.no_internet_connection)).show()
                                            isDisable = false
                                            checkUpdateProgress.isVisible = false
                                        }
                                    }
                                }
                            }
                        } marginLeft 8.0
                        later {
                            checkUpdateProgress = progressIndicator {
                                maxSize = checkUpdateButton.height
                                isVisible = false
                            } marginLeft 8.0
                        }
                    } marginTop 20.0
                } marginLeft 48.0
            }
        }
        dialogPane.expandableContent = ktfx.layouts.hbox {
            titledPane(getString(R.string.open_source_software)) {
                isCollapsible = false
                licenseList = listView {
                    prefHeight = 256.0
                    items = License.values().toObservableList()
                    cellFactory {
                        onUpdate { license, empty ->
                            if (license != null && !empty) graphic = ktfx.layouts.vbox {
                                label(license.repo) { font = font(12.0) }
                                label(license.owner) { font = getFont(R.font.sf_pro_text_bold, 12) }
                            }
                        }
                    }
                }
            }
            titledPane(getString(R.string.license)) {
                isCollapsible = false
                textArea {
                    prefHeight = 256.0
                    isEditable = false
                    text = getString(R.string.select_license)
                    licenseList.selectionModel.selectedItemProperty().listener { _, _, license ->
                        text = license?.getContent() ?: getString(R.string.select_license)
                    }
                }
            }
        }
        customButton("Homepage", CANCEL_CLOSE) {
            visibleProperty().bind(dialogPane.expandedProperty() and selectedBinding)
            onActionFilter { browseUrl(selected!!.homepage) }
        }
        closeButton()
    }

    override val selectionModel: SelectionModel<License> get() = licenseList.selectionModel
}