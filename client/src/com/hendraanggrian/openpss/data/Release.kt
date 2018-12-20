package com.hendraanggrian.openpss.data

import org.apache.maven.artifact.versioning.ComparableVersion

data class Release(
    val name: String,
    val assets: List<Asset>
) {

    fun isNewerThan(currentVersion: String): Boolean =
        ComparableVersion(name) > ComparableVersion(currentVersion) &&
            assets.isNotEmpty() &&
            assets.all { it.isUploaded() }
}