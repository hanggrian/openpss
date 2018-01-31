package com.wijayaprinting.db.dao

import com.wijayaprinting.db.Ided
import com.wijayaprinting.db.schema.Configs
import kotlinx.nosql.Id

data class Config(
        val key: String,
        val value: String
) : Ided<Configs> {
    override lateinit var id: Id<String, Configs>

    companion object {
        const val KEY_TIMEZONE = "timezone"
        const val DEFAULT_TIMEZONE="Asia/Jakarta"
    }
}