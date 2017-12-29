package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.internal.Language
import com.wijayaprinting.manager.internal.Resourceful
import com.wijayaprinting.manager.io.PreferencesFile
import java.util.*

/** Base class of all controllers. */
abstract class Controller : Resourceful {

    override val resources: ResourceBundle = Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value).getResources("string")

    /** Equivalent to user data in [javafx.scene.Node]. */
    private lateinit var userData: Any

    fun setUserData(value: Any) {
        userData = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getUserData() = userData as T
}