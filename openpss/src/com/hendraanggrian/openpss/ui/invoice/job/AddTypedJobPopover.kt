package com.hendraanggrian.openpss.ui.invoice.job

import com.hendraanggrian.openpss.content.Context
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.PlatePrice
import javafx.scene.control.ComboBox

abstract class AddTypedJobPopover<T : Invoice.TypedJob>(context: Context, titleId: String) :
    AddJobPopover<T>(context, titleId), Invoice.TypedJob {

    private lateinit var typeChoice: ComboBox<PlatePrice>
}