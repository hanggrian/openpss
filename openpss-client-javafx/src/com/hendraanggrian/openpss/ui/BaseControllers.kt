package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.OpenPssApp
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.SettingsFile
import com.hendraanggrian.openpss.schema.Employee
import com.hendraanggrian.prefy.Prefy
import com.hendraanggrian.prefy.jvm.PropertiesPreferences
import com.hendraanggrian.prefy.jvm.get
import java.net.URL
import java.util.Properties
import java.util.ResourceBundle
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.layout.StackPane
import ktfx.getValue
import ktfx.layouts.NodeManager
import ktfx.setValue

/** Base class of all controllers. */
@Suppress("LeakingThis")
open class BaseController : Initializable, FxComponent {

    private lateinit var _prefs: PropertiesPreferences
    override val prefs: PropertiesPreferences
        get() {
            if (!::_prefs.isInitialized) _prefs = Prefy[SettingsFile]
            return _prefs
        }

    override lateinit var resourceBundle: ResourceBundle
    override val valueProperties: Properties = OpenPssApp::class.java
        .getResourceAsStream(R.value._value)
        .use { stream -> Properties().apply { load(stream) } }

    override lateinit var rootLayout: StackPane
    override lateinit var login: Employee

    private lateinit var extras: MutableMap<String, Any>

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.resourceBundle = resources
        (this as? Refreshable)?.refresh()
    }

    /** Register extra [value] with [key]. */
    fun addExtra(key: String, value: Any): BaseController {
        if (!::extras.isInitialized) {
            extras = mutableMapOf()
        }
        extras[key] = value
        return this
    }

    /** Get extra registered with [key], should be executed in platform thread. */
    fun <T : Any> getExtra(key: String): T {
        check(::extras.isInitialized) { "No extras found in this controller" }
        @Suppress("UNCHECKED_CAST") return checkNotNull(extras[key] as T) { "No extra found with that key." }
    }
}

open class ActionController : BaseController() {

    private val titleProperty: StringProperty = SimpleStringProperty(null)
    fun titleProperty(): StringProperty = titleProperty
    var title: String? by titleProperty

    val actions = mutableListOf<Node>()

    private val actionManager = object : NodeManager {
        override fun <T : Node> addChild(child: T): T = child.also { actions += it }
    }

    /** Override this function to add actions. */
    open fun NodeManager.onCreateActions() {
    }

    override fun initialize(location: URL, resources: ResourceBundle) {
        super.initialize(location, resources)
        actionManager.onCreateActions()
    }
}
