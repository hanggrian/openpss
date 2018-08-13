package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.FULL_NAME
import com.hendraanggrian.openpss.BuildConfig.USER
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.BuildConfig.WEBSITE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.Dialog
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.main.License
import com.hendraanggrian.openpss.util.bold
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
import javafxx.application.later
import javafxx.beans.value.and
import javafxx.collections.toObservableList
import javafxx.coroutines.FX
import javafxx.coroutines.listener
import javafxx.coroutines.onAction
import javafxx.layouts.button
import javafxx.layouts.hbox
import javafxx.layouts.hyperlink
import javafxx.layouts.imageView
import javafxx.layouts.label
import javafxx.layouts.progressIndicator
import javafxx.layouts.text
import javafxx.layouts.textFlow
import javafxx.layouts.vbox
import javafxx.listeners.cellFactory
import javafxx.scene.control.closeButton
import javafxx.scene.control.customButton
import javafxx.scene.control.errorAlert
import javafxx.scene.control.icon
import javafxx.scene.control.styledInfoAlert
import javafxx.scene.layout.maxSize
import javafxx.scene.layout.paddingAll
import kotlinx.coroutines.experimental.launch
import org.controlsfx.control.MasterDetailPane
import java.util.concurrent.TimeUnit.SECONDS

class AboutDialog(resourced: Resourced) : Dialog<Nothing>(resourced), Selectable<License> {

    private lateinit var checkUpdateButton: Button
    private lateinit var checkUpdateProgress: ProgressIndicator
    private val licenseList: ListView<License> = javafxx.layouts.listView(License.values().toObservableList()) {
        cellFactory {
            onUpdate { license, empty ->
                if (license != null && !empty) graphic = javafxx.layouts.vbox {
                    label(license.repo) { font = font(12.0) }
                    label(license.owner) { font = bold(12) }
                }
            }
        }
    }

    init {
        icon = Image(R.image.menu_about)
        title = getString(R.string.about)
        dialogPane.run {
            stylesheets += getStyle(R.style.openpss)
            content = hbox {
                paddingAll = 48.0
                imageView(Image(R.image.display_launcher)) {
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
                        USER { font = bold(12) }
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
                                                    title = getString(R.string.openpss_is_available, release.version),
                                                    buttonTypes = *arrayOf(CANCEL)
                                                ) {
                                                    dialogPane.content = javafxx.layouts.vbox {
                                                        release.assets.forEach { asset ->
                                                            hyperlink(asset.name) {
                                                                onAction { _ ->
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
        dialogPane.expandableContent = javafxx.layouts.titledPane(getString(R.string.open_source_software_license)) {
            isCollapsible = false
            MasterDetailPane().apply {
                maxHeight = 256.0
                dividerPosition = 0.3
                showDetailNodeProperty().bind(selectedBinding)
                masterNode = licenseList
                detailNode = javafxx.layouts.textArea {
                    isEditable = false
                    selectedProperty.listener { _, _, license -> text = license?.getContent() }
                }
            }()
        }
        customButton("Homepage", CANCEL_CLOSE) {
            visibleProperty().bind(dialogPane.expandedProperty() and selectedBinding)
            onActionFilter { browseUrl(selected!!.homepage) }
        }
        closeButton()
    }

    override val selectionModel: SelectionModel<License> get() = licenseList.selectionModel
}