package com.wijayaprinting.manager.controller

import com.wijayaprinting.Registrable
import com.wijayaprinting.manager.internal.Language
import com.wijayaprinting.manager.Resourceful
import com.wijayaprinting.manager.io.PreferencesFile
import io.reactivex.disposables.Disposable
import java.util.*

/** Base class of all controllers. */
abstract class Controller : Resourceful, Registrable {

    override val resources: ResourceBundle = Language.parse(PreferencesFile.language.value).getResources("string")
    override val disposables: MutableSet<Disposable> = mutableSetOf()

    private var mExtra: Any? = null

    fun setExtra(value: Any?) {
        mExtra = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getExtra(): T = checkNotNull(mExtra as T) { "User data has not been initialized!" }
}