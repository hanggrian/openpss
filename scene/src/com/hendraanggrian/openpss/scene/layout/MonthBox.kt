@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package com.hendraanggrian.openpss.scene.layout

import com.hendraanggrian.openpss.scene.R
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.image.ImageView
import ktfx.beans.binding.bindingOf
import ktfx.beans.value.and
import ktfx.beans.value.eq
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
import java.util.Locale.US

open class MonthBox(prefill: YearMonth = now()) : _HBox() {

    private companion object {
        const val YEAR_START = 2018
        const val YEAR_END = 2050
    }

    lateinit var monthBox: ChoiceBox<Int>
    lateinit var yearBox: ChoiceBox<Int>
    var previousButton: Button
    var nextButton: Button

    val valueProperty: ObjectProperty<YearMonth> = SimpleObjectProperty()
    val value: YearMonth by valueProperty

    init {
        spacing = 8.0

        previousButton = button(graphic = ImageView(R.image.btn_previous)) {
            onAction {
                monthBox.value = when (monthBox.value) {
                    0 -> {
                        yearBox.value = yearBox.value - 1
                        11
                    }
                    else -> monthBox.value - 1
                }
            }
        }
        monthBox = choiceBox((0 until 12).toObservableList()) {
            value = prefill.monthOfYear - 1
        }
        setLocale(US)
        yearBox = choiceBox((YEAR_START until YEAR_END).toObservableList()) {
            value = items.single { it == prefill.year }
        }
        nextButton = button(graphic = ImageView(R.image.btn_next)) {
            onAction {
                monthBox.selectionModel.run {
                    monthBox.value = when (monthBox.value) {
                        11 -> {
                            yearBox.value = yearBox.value + 1
                            0
                        }
                        else -> monthBox.value + 1
                    }
                }
            }
        }

        previousButton.disableProperty().bind(monthBox.selectionModel.selectedIndexProperty().eq(0)
            and yearBox.valueProperty().eq(YEAR_START))
        nextButton.disableProperty().bind(monthBox.selectionModel.selectedIndexProperty().eq(11)
            and yearBox.valueProperty().eq(YEAR_END))

        valueProperty.bind(bindingOf(monthBox.selectionModel.selectedIndexProperty(), yearBox.valueProperty()) {
            YearMonth(yearBox.value, monthBox.selectionModel.selectedIndex + 1)
        })
    }

    fun setLocale(locale: Locale) = getInstance(locale).let { symbols ->
        monthBox.converter {
            fromString { symbols.months.indexOf(it) }
            toString { symbols.months[it!!] }
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
): MonthBox = com.hendraanggrian.openpss.scene.layout.monthBox(prefill, init).add()