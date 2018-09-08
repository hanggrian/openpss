package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.BuildConfig.FULL_NAME
import com.hendraanggrian.openpss.BuildConfig.USER
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.BuildConfig.WEBSITE
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.control.dialog.Dialog
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.ui.Selectable
import com.hendraanggrian.openpss.ui.main.License
import com.hendraanggrian.openpss.control.bold
import com.hendraanggrian.openpss.util.desktop
import com.hendraanggrian.openpss.util.getFont
import com.hendraanggrian.openpss.util.getStyle
import com.hendraanggrian.openpss.control.onActionFilter
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE
import javafx.scene.control.ListView
import javafx.scene.control.SelectionModel
import javafx.scene.image.Image
import javafx.scene.text.Font.font
import javafxx.beans.value.and
import javafxx.collections.toObservableList
import javafxx.coroutines.listener
import javafxx.coroutines.onAction
import javafxx.layouts.button
import javafxx.layouts.hbox
import javafxx.layouts.imageView
import javafxx.layouts.label
import javafxx.layouts.text
import javafxx.layouts.textFlow
import javafxx.layouts.vbox
import javafxx.listeners.cellFactory
import javafxx.scene.control.closeButton
import javafxx.scene.control.customButton
import javafxx.scene.control.icon
import javafxx.scene.layout.paddingAll
import org.controlsfx.control.MasterDetailPane
import java.net.URI

class AboutDialog(resourced: Resourced) : Dialog<Nothing>(resourced), Selectable<License> {

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
                        USER { font = bold(12) }
                    } marginTop 4.0
                    hbox {
                        spacing = R.dimen.padding_small.toDouble()
                        button("GitHub") { onAction { desktop?.browse(URI(WEBSITE)) } }
                        button("Email") { onAction { desktop?.mail(URI("mailto:$USER@gmail")) } }
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
            onActionFilter { desktop?.browse(URI(selected!!.homepage)) }
        }
        closeButton()
    }

    override val selectionModel: SelectionModel<License> get() = licenseList.selectionModel
}