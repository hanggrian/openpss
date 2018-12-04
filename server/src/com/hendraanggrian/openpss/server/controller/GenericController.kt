package com.hendraanggrian.openpss.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

interface GenericController {

    @GetMapping("/listall")
    fun listAll(
        @RequestParam(value = "table") table: String
    ) {
    }
}