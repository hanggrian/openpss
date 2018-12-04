package com.hendraanggrian.openpss.server.controller

import com.hendraanggrian.openpss.api.Page
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.server.db.transaction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import kotlin.math.ceil

@Suppress("unused")
interface LogController {

    @GetMapping("/logs")
    fun getLogs(
        @RequestParam(value = "page") page: Int,
        @RequestParam(value = "count") count: Int
    ): Page<Log> = transaction {
        val logs = Logs()
        Page(
            ceil(logs.count() / count.toDouble()).toInt(),
            logs.skip(count * page).take(count).toList()
        )
    }
}