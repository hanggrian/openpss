package com.hendraanggrian.openpss.server.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller : AuthController {

    @RequestMapping("/login")
    override fun login(
        @RequestParam(value = "name") name: String,
        @RequestParam(value = "password") password: String
    ) = super.login(name, password)
}