package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.api.github.Release
import com.hendraanggrian.openpss.internal.ClientBuildConfig
import io.ktor.client.request.get

/** GitHub API used to check latest version. */
class GitHubApi : Api("https://api.github.com") {

    suspend fun getLatestRelease(): Release = client.get {
        apiUrl("repos/${ClientBuildConfig.USER}/${ClientBuildConfig.ARTIFACT}/releases/latest")
    }
}