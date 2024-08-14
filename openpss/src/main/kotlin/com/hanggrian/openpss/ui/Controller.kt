package com.hanggrian.openpss.ui

import com.hanggrian.openpss.Context
import com.hanggrian.openpss.R
import com.hanggrian.openpss.db.schemas.Employee
import javafx.fxml.Initializable
import javafx.scene.layout.StackPane
import java.net.URL
import java.util.Properties
import java.util.ResourceBundle

/** Base class of all controllers. */
open class Controller :
    Initializable,
    Context {
    override lateinit var resourceBundle: ResourceBundle
    override val dimenResources: Properties = getProperties(R.dimen_dimen)
    override val colorResources: Properties = getProperties(R.color_color)

    override lateinit var stack: StackPane
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
        return checkNotNull(extras[key] as T) { "No extra found with that key." }
    }
}
