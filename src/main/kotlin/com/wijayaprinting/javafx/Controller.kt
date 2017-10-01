package com.wijayaprinting.javafx

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import java.net.URL
import java.util.*

/**
 * Each controller is attached to a stage.
 * Use [Controller.inflate] to automatically supply next controller with resources and extra value (if any).
 *
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class Controller<T> : Resourced {

    companion object {
        fun inflate(location: URL, resources: ResourceBundle) = inflate<Any>(location, resources, null)

        fun <T> inflate(location: URL, resources: ResourceBundle, extra: T?): Parent {
            val loader = FXMLLoader(location, resources)
            val root = loader.load<Parent>()
            loader.getController<Controller<T>>().apply {
                mExtra = extra
                mResources = resources
            }
            return root
        }
    }

    private var mExtra: T? = null
    private var mResources: ResourceBundle? = null

    val extra: T get() = mExtra!!
    override val resources: ResourceBundle get() = mResources!!
}