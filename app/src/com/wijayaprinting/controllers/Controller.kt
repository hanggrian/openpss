package com.wijayaprinting.controllers

import com.wijayaprinting.core.Resourced
import com.wijayaprinting.data.Language
import com.wijayaprinting.io.PreferencesFile
import java.util.*

/** Base class of all controllers. */
abstract class Controller : Resourced {

    override val resources: ResourceBundle = Language.valueOf(PreferencesFile.language.get()).getResources("string")

    private var mExtra: Any? = null

    fun setExtra(value: Any?) {
        mExtra = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getExtra(): T = checkNotNull(mExtra as T) { "User data has not been initialized!" }
}