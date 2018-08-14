package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.i18n.Resourced
import com.hendraanggrian.openpss.util.desktop
import com.hendraanggrian.openpss.util.getStyle
import javafx.scene.control.ButtonType
import javafxx.coroutines.FX
import javafxx.coroutines.onAction
import javafxx.layouts.hyperlink
import javafxx.scene.control.errorAlert
import javafxx.scene.control.styledInfoAlert
import kotlinx.coroutines.experimental.launch
import java.net.URI
import java.util.concurrent.TimeUnit

object UpdateChecker {

    fun check(resourced: Resourced, onFinished: (() -> Unit)? = null) {
        launch {
            try {
                val release = GitHubApi.create().getLatestRelease().get(10, TimeUnit.SECONDS)
                launch(FX) {
                    when {
                        release.isNewer() -> styledInfoAlert(getStyle(R.style.openpss),
                            title = resourced.getString(R.string.openpss_is_available, release.version),
                            buttonTypes = *arrayOf(ButtonType.CANCEL)) {
                            dialogPane.content = javafxx.layouts.vbox {
                                release.assets.forEach { asset ->
                                    hyperlink(asset.name) {
                                        onAction { _ ->
                                            desktop?.browse(URI(asset.downloadUrl))
                                            close()
                                        }
                                    }
                                }
                            }
                        }.show()
                        else -> styledInfoAlert(
                            getStyle(R.style.openpss),
                            resourced.getString(R.string.you_re_up_to_date),
                            contentText = resourced.getString(
                                R.string.openpss_is_currently_the_newest_version_available,
                                BuildConfig.VERSION)
                        ).show()
                    }
                    onFinished?.invoke()
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                launch(FX) {
                    errorAlert(resourced.getString(R.string.no_internet_connection)).show()
                    onFinished?.invoke()
                }
            }
        }
    }
}