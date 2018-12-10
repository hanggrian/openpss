package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.api.route.AuthRoute
import com.hendraanggrian.openpss.api.route.CustomerRoute
import com.hendraanggrian.openpss.api.route.InvoiceRoute
import com.hendraanggrian.openpss.api.route.LogRoute
import com.hendraanggrian.openpss.api.route.NamedRoute
import com.hendraanggrian.openpss.api.route.RecessRoute

/** Main API. */
class OpenPSSApi : Api("http://localhost:8080"),
    AuthRoute,
    CustomerRoute,
    InvoiceRoute,
    LogRoute,
    NamedRoute,
    RecessRoute