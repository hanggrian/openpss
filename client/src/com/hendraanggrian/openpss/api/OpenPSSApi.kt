package com.hendraanggrian.openpss.api

import com.hendraanggrian.openpss.route.AuthRoute
import com.hendraanggrian.openpss.route.CustomerRoute
import com.hendraanggrian.openpss.route.GlobalSettingRoute
import com.hendraanggrian.openpss.route.InvoiceRoute
import com.hendraanggrian.openpss.route.LogRoute
import com.hendraanggrian.openpss.route.NamedRoute
import com.hendraanggrian.openpss.route.PaymentRoute
import com.hendraanggrian.openpss.route.RecessRoute
import com.hendraanggrian.openpss.route.WageRoute

/** Main API. */
class OpenPSSApi : Api("http://localhost:8080"),
    AuthRoute,
    CustomerRoute,
    GlobalSettingRoute,
    InvoiceRoute,
    LogRoute,
    NamedRoute,
    PaymentRoute,
    RecessRoute,
    WageRoute