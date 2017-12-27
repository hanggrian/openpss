package com.wijayaprinting.manager.controller

import com.wijayaprinting.manager.Language
import com.wijayaprinting.manager.Resourceful
import com.wijayaprinting.manager.io.PreferencesFile
import java.util.*

abstract class Controller : Resourceful {

    override val resources: ResourceBundle = Language.parse(PreferencesFile[PreferencesFile.LANGUAGE].value).getResources("string")
}