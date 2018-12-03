package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.content.FxComponent
import com.hendraanggrian.openpss.db.schemas.Employee
import javafx.fxml.Initializable
import javafx.scene.layout.StackPane
import java.net.URL
import java.util.Properties
import java.util.ResourceBundle

/** Base class of all controllers. */
@Suppress("LeakingThis")
open class Controller : Initializable, FxComponent {

    override lateinit var resourceBundle: ResourceBundle
    override val dimenResources: Properties = getProperties(R.dimen.properties_dimen)
    override val colorResources: Properties = getProperties(R.color.properties_color)

    override lateinit var rootLayout: StackPane
    override lateinit var login: Employee

    private lateinit var extras: MutableMap<String, Any>

    override fun initialize(location: URL, resources: ResourceBundle) {
        this.resourceBundle = resources
        (this as? Refreshable)?.refresh()
    }

    /** Register extra [value] with [key]. */
    fun addExtra(key: String, value: Any): Controller {
        if (!::extras.isInitialized) {
            extras = mutableMapOf()
        }
        extras[key] = value
        return this
    }

    /** Get extra registered with [key], should be executed in platform thread. */
    fun <T : Any> getExtra(key: String): T {
        check(::extras.isInitialized) { "No extras found in this controller." }
        @Suppress("UNCHECKED_CAST") return checkNotNull(extras[key] as T) { "No extra found with that key." }
    }
}