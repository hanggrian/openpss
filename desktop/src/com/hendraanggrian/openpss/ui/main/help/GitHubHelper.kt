package com.hendraanggrian.openpss.ui.main.help

import com.hendraanggrian.openpss.App
import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.BuildConfig.DEBUG
import com.hendraanggrian.openpss.BuildConfig.VERSION
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.api.GitHubApi
import com.hendraanggrian.openpss.content.FxComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import ktfx.jfoenix.jfxSnackbar
import java.lang.ref.WeakReference
import java.net.URI

/** As seen in `https://developer.github.com/v3/`. */
object GitHubHelper {

    private var apiRef = WeakReference<GitHubApi?>(null)

    fun checkUpdates(component: FxComponent) {
        var api = apiRef.get()
        if (api == null) {
            api = GitHubApi()
            apiRef = WeakReference(api)
        }
        GlobalScope.launch(Dispatchers.JavaFx) {
            try {
                val release = api.getLatestRelease()
                when {
                    release.isNewerThan(BuildConfig.VERSION) -> component.rootLayout.jfxSnackbar(
                        component.getString(R.string.openpss_is_available, release.version),
                        App.DURATION_LONG,
                        component.getString(R.string.download)
                    ) {
                        UpdateDialog(component, release.assets).show { url ->
                            component.desktop?.browse(URI(url))
                        }
                    }
                    else -> component.rootLayout.jfxSnackbar(
                        component.getString(
                            R.string.openpss_is_currently_the_newest_version_available,
                            VERSION
                        ),
                        App.DURATION_SHORT
                    )
                }
            } catch (e: Exception) {
                if (DEBUG) e.printStackTrace()
                component.rootLayout.jfxSnackbar(
                    component.getString(R.string.no_internet_connection),
                    App.DURATION_SHORT
                )
            }
        }
    }
}