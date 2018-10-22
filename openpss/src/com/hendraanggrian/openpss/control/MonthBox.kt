@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.control

import com.hendraanggrian.openpss.R
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.geometry.Pos.CENTER
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.image.ImageView
import ktfx.NodeManager
import ktfx.annotations.LayoutDsl
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.getValue
import ktfx.collections.toObservableList
import ktfx.coroutines.onAction
import ktfx.jfoenix.jfxButton
import ktfx.layouts._HBox
import ktfx.layouts.choiceBox
import ktfx.listeners.converter
import org.joda.time.YearMonth
import org.joda.time.YearMonth.now
import java.text.DateFormatSymbols.getInstance
import java.util.Locale

open class MonthBox @JvmOverloads constructor(prefill: YearMonth = now()) : _HBox(0.0) {

    lateinit var monthBox: ChoiceBox<Int>
    lateinit var yearField: IntField
    var previousButton: Button
    var nextButton: Button

    private val valueProperty = SimpleObjectProperty<YearMonth>()
    fun valueProperty(): ObjectProperty<YearMonth> = valueProperty
    val value: YearMonth by valueProperty

    private var months: Array<String> = getInstance().shortMonths

    init {
        alignment = Pos.CENTER
        previousButton = jfxButton(graphic = ImageView(R.image.btn_previous)) {
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
            alignment = CENTER
            maxWidth = 56.0
            value = prefill.year
        }
        nextButton = jfxButton(graphic = ImageView(R.image.btn_next)) {
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

        valueProperty.bind(bindingOf(monthBox.selectionModel.selectedIndexProperty(), yearField.valueProperty()) {
            YearMonth(yearField.value, monthBox.value + 1)
        })
    }

    fun setLocale(locale: Locale) {
        months = getInstance(locale).shortMonths
        monthBox.converter {
            fromString { months.indexOf(it) }
            toString { months[it!!] }
        }
    }
}

/** Creates a [MonthBox]. */
fun monthBox(
    prefill: YearMonth = YearMonth.now(),
    init: ((@LayoutDsl MonthBox).() -> Unit)? = null
): MonthBox = MonthBox(prefill).also {
    init?.invoke(it)
}

/** Creates a [MonthBox] and add it to this [LayoutManager]. */
inline fun NodeManager.monthBox(
    prefill: YearMonth = YearMonth.now(),
    noinline init: ((@LayoutDsl MonthBox).() -> Unit)? = null
): MonthBox = (com.hendraanggrian.openpss.control.monthBox(prefill, init))()