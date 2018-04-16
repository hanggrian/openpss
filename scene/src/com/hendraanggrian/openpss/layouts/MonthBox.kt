@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.layouts

import com.hendraanggrian.openpss.scene.R
import com.hendraanggrian.openpss.controls.IntField
import com.hendraanggrian.openpss.controls.intField
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.getValue
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.layouts.LayoutDsl
import ktfx.layouts.LayoutManager
import ktfx.layouts._HBox
import ktfx.layouts.button
import ktfx.layouts.choiceBox
import ktfx.listeners.converter
import org.joda.time.YearMonth
import org.joda.time.YearMonth.now
import java.text.DateFormatSymbols.getInstance
import java.util.Locale

open class MonthBox(prefill: YearMonth = now()) : _HBox() {

    lateinit var monthBox: ChoiceBox<Int>
    lateinit var yearField: IntField
    var previousButton: Button
    var nextButton: Button

    val valueProperty: ObjectProperty<YearMonth> = SimpleObjectProperty()
    val value: YearMonth by valueProperty

    private var months: Array<String> = getInstance().months

    init {
        spacing = 8.0

        previousButton = button(graphic = ImageView(R.image.btn_previous)) {
            onAction {
                monthBox.value = when (monthBox.value) {
                    0 -> {
                        yearField.value = yearField.value - 1
                        11
                    }
                    else -> monthBox.value - 1
                }
            }
        }
        monthBox = choiceBox((0 until 12).toObservableList()) {
            value = prefill.monthOfYear - 1
            converter {
                toString { months[it!!] }
                fromString { months.indexOf(it) }
            }
        }
        yearField = intField {
            maxWidth = 64.0
            value = prefill.year
        }
        nextButton = button(graphic = ImageView(R.image.btn_next)) {
            onAction {
                monthBox.value = when (monthBox.value) {
                    11 -> {
                        yearField.value = yearField.value + 1
                        0
                    }
                    else -> monthBox.value + 1
                }
            }
        }

        valueProperty.bind(bindingOf(monthBox.selectionModel.selectedIndexProperty(), yearField.valueProperty) {
            YearMonth(yearField.value, monthBox.value + 1)
        })
    }

    fun setLocale(locale: Locale) {
        months = getInstance(locale).months
        monthBox.converter {
            fromString { months.indexOf(it) }
            toString { months[it!!] }
        }
    }
}

inline fun monthBox(
    prefill: YearMonth = now()
): MonthBox = monthBox(prefill) { }

inline fun monthBox(
    prefill: YearMonth = now(),
    init: (@LayoutDsl MonthBox).() -> Unit
): MonthBox = MonthBox(prefill).apply(init)

inline fun LayoutManager<Node>.monthBox(
    prefill: YearMonth = now()
): MonthBox = monthBox(prefill) { }

inline fun LayoutManager<Node>.monthBox(
    prefill: YearMonth = now(),
    init: (@LayoutDsl MonthBox).() -> Unit
): MonthBox = com.hendraanggrian.openpss.layouts.monthBox(prefill, init).add()