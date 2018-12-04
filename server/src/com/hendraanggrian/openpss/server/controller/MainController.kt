package com.hendraanggrian.openpss.server.controller

import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("unused")
class MainController : AuthController, LogController, CustomerController