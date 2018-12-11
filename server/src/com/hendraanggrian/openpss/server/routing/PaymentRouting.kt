package com.hendraanggrian.openpss.server.routing

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.db.schemas.Invoices
import com.hendraanggrian.openpss.db.schemas.Log
import com.hendraanggrian.openpss.db.schemas.Logs
import com.hendraanggrian.openpss.db.schemas.Payment
import com.hendraanggrian.openpss.db.schemas.Payments
import com.hendraanggrian.openpss.server.db.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.post

object PaymentRouting : Routing {

    override fun RouteWrapper.onInvoke() {
        "payments" {
            post {
                val payment = call.receive<Payment>()
                payment.id = transaction { Payments.insert(payment) }
                call.respond(payment)
            }
            delete {
                val payment = call.receive<Payment>()
                transaction {
                    val invoiceNo = Invoices[payment.invoiceId].single().no
                    Payments -= payment.id
                    Logs += Log.new(
                        resources.getString(R.string.payment_delete).format(payment.value, invoiceNo),
                        call.getString("login")
                    )
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}