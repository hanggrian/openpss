package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema

open class Order<D : Any, S : DocumentSchema<D>> : Ided<S> {
    override lateinit var id: Id<String, S>
    open var total: Double = 0.0
}