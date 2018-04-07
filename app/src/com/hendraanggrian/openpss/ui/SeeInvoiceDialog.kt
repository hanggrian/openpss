package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.currencyConverter
import com.hendraanggrian.openpss.db.findById
import com.hendraanggrian.openpss.db.schemas.Config.Companion.KEY_INVOICE_SUBTITLE1
import com.hendraanggrian.openpss.db.schemas.Config.Companion.KEY_INVOICE_SUBTITLE2
import com.hendraanggrian.openpss.db.schemas.Config.Companion.KEY_INVOICE_SUBTITLE3
import com.hendraanggrian.openpss.db.schemas.Config.Companion.KEY_INVOICE_TITLE
import com.hendraanggrian.openpss.db.schemas.Customers
import com.hendraanggrian.openpss.db.schemas.Employees
import com.hendraanggrian.openpss.db.schemas.Invoice
import com.hendraanggrian.openpss.db.schemas.findConfig
import com.hendraanggrian.openpss.db.transaction
import com.hendraanggrian.openpss.numberConverter
import com.hendraanggrian.openpss.time.PATTERN_DATETIME_EXTENDED
import com.hendraanggrian.openpss.utils.getFont
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.geometry.Pos.CENTER_RIGHT
import javafx.scene.Node
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority.ALWAYS
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.WHITE
import javafx.scene.shape.Line
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._GridPane
import ktfx.layouts._HBox
import ktfx.layouts._VBox
import ktfx.layouts.gridPane
import ktfx.layouts.hbox
import ktfx.layouts.label
import ktfx.layouts.line
import ktfx.layouts.region
import ktfx.layouts.vbox
import ktfx.scene.control.closeButton
import ktfx.scene.control.graphicIcon
import ktfx.scene.control.headerTitle

class SeeInvoiceDialog(resourced: Resourced, invoice: Invoice) : Dialog<Unit>(), Resourced by resourced {

    private companion object {
        const val MAX_WIDTH = 285.0 // equivalent to 7.5cm
    }

    init {
        headerTitle = getString(R.string.see_invoice)
        graphicIcon = ImageView(R.image.ic_invoice)
        dialogPane.content = maxedVbox(CENTER) {
            background = Background(BackgroundFill(WHITE, CornerRadii.EMPTY, Insets.EMPTY))
            transaction {
                optionalLabel(findConfig(KEY_INVOICE_TITLE), R.font.opensans_bold)
                optionalLabel(findConfig(KEY_INVOICE_SUBTITLE1), R.font.opensans_regular)
                optionalLabel(findConfig(KEY_INVOICE_SUBTITLE2), R.font.opensans_regular)
                optionalLabel(findConfig(KEY_INVOICE_SUBTITLE3), R.font.opensans_regular)
                straightLine()
                maxedVbox(CENTER) {
                    boldLabel(invoice.id.toString())
                    regularLabel(invoice.dateTime.toString(PATTERN_DATETIME_EXTENDED))
                    maxedGridPane {
                        regularLabel(getString(R.string.employee)) row 0 col 0
                        boldLabel(findById(Employees, invoice.employeeId).single().toString()) row 0 col 1
                        regularLabel(getString(R.string.customer)) row 1 col 0
                        findById(Customers, invoice.customerId).single().let {
                            boldLabel(it.name) row 1 col 1
                            regularLabel(it.id.toString()) row 2 col 1
                        }
                    }
                }
                invoice.plates.run {
                    if (isNotEmpty()) {
                        straightLine()
                        maxedVbox {
                            boldLabel(getString(R.string.plate))
                            maxedVbox(CENTER_LEFT) {
                                forEach {
                                    regularLabel(it.title)
                                    maxedHbox(CENTER_LEFT) {
                                        regularLabel("  ${it.type} ${numberConverter.toString(it.qty)} x " +
                                            currencyConverter.toString(it.price))
                                        region() hpriority ALWAYS
                                        regularLabel(currencyConverter.toString(it.total))
                                    }
                                }
                            }
                        }
                    }
                }
                invoice.offsets.run {
                    if (isNotEmpty()) {
                        straightLine()
                        maxedVbox {
                            boldLabel(getString(R.string.offset))
                            maxedVbox(CENTER_LEFT) {
                                forEach {
                                    regularLabel(it.title)
                                    maxedHbox(CENTER_LEFT) {
                                        regularLabel("  ${it.type} ${numberConverter.toString(it.qty)}")
                                        region() hpriority ALWAYS
                                        regularLabel(currencyConverter.toString(it.total))
                                    }
                                }
                            }
                        }
                    }
                }
                invoice.others.run {
                    if (isNotEmpty()) {
                        straightLine()
                        maxedVbox {
                            boldLabel(getString(R.string.others))
                            maxedVbox(CENTER_LEFT) {
                                forEach {
                                    regularLabel(it.title)
                                    maxedHbox(CENTER_LEFT) {
                                        regularLabel("  ${numberConverter.toString(it.qty)} x " +
                                            currencyConverter.toString(it.price))
                                        region() hpriority ALWAYS
                                        regularLabel(currencyConverter.toString(it.total))
                                    }
                                }
                            }
                        }
                    }
                }
                straightLine()
                maxedHbox(CENTER_RIGHT) {
                    boldLabel("${getString(R.string.total)} ${currencyConverter.toString(invoice.total)}")
                }
            }
        }
        closeButton()
    }

    private inline fun maxedVbox(
        pos: Pos = CENTER,
        init: (@LayoutDsl _VBox).() -> Unit
    ): VBox = vbox {
        minWidth = MAX_WIDTH
        maxWidth = MAX_WIDTH
        alignment = pos
        init()
    }

    private inline fun LayoutManager<Node>.maxedVbox(
        pos: Pos = CENTER,
        init: (@LayoutDsl _VBox).() -> Unit
    ): VBox = vbox {
        minWidth = MAX_WIDTH
        maxWidth = MAX_WIDTH
        alignment = pos
        init()
    }

    private inline fun LayoutManager<Node>.maxedHbox(
        pos: Pos = CENTER,
        init: (@LayoutDsl _HBox).() -> Unit
    ): HBox = hbox {
        minWidth = MAX_WIDTH
        maxWidth = MAX_WIDTH
        alignment = pos
        init()
    }

    private fun LayoutManager<Node>.straightLine(): Line = line(endX = MAX_WIDTH)

    private fun LayoutManager<Node>.optionalLabel(text: String, fontRes: String) {
        if (text.isNotBlank()) label(text) { font = getFont(fontRes) }
    }

    private inline fun LayoutManager<Node>.maxedGridPane(init: (@LayoutDsl _GridPane).() -> Unit): GridPane = gridPane {
        minWidth = MAX_WIDTH
        maxWidth = MAX_WIDTH
        hgap = 8.0
        init()
    }

    private fun LayoutManager<Node>.regularLabel(text: String): Label = label(text) {
        font = getFont(R.font.opensans_regular)
    }

    private fun LayoutManager<Node>.boldLabel(text: String): Label = label(text) {
        font = getFont(R.font.opensans_bold)
    }
}