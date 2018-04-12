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
import org.joda.time.YearMonth
import org.joda.time.YearMonth.now
import java.text.DateFormatSymbols.getInstance

open class MonthBox(prefill: YearMonth = now()) : _HBox() {

    private companion object {
        const val YEAR_START = 2018
        const val YEAR_END = 2050
    }

    lateinit var monthBox: ChoiceBox<String>
    lateinit var yearBox: ChoiceBox<Int>
    var previousButton: Button
    var nextButton: Button

    val monthProperty: ObjectProperty<YearMonth> = SimpleObjectProperty()
    val month: YearMonth by monthProperty

    init {
        spacing = 8.0

        previousButton = button(graphic = ImageView(R.image.btn_previous)) {
            onAction {
                monthBox.selectionModel.run {
                    when (selectedIndex) {
                        0 -> {
                            select(11)
                            yearBox.value = yearBox.value - 1
                        }
                        else -> select(selectedIndex - 1)
                    }
                }
            }
        }
        monthBox = choiceBox(getInstance().months.take(12).toObservableList()) {
            selectionModel.select(prefill.monthOfYear - 1)
        }
        yearBox = choiceBox((YEAR_START until YEAR_END).toObservableList()) {
            value = items.single { it == prefill.year }
        }
        nextButton = button(graphic = ImageView(R.image.btn_next)) {
            onAction {
                monthBox.selectionModel.run {
                    when (selectedIndex) {
                        11 -> {
                            select(0)
                            yearBox.value = yearBox.value + 1
                        }
                        else -> select(selectedIndex + 1)
                    }
                }
            }
        }

        previousButton.disableProperty().bind(monthBox.selectionModel.selectedIndexProperty().eq(0)
            and yearBox.valueProperty().eq(YEAR_START))
        nextButton.disableProperty().bind(monthBox.selectionModel.selectedIndexProperty().eq(11)
            and yearBox.valueProperty().eq(YEAR_END))

        monthProperty.bind(bindingOf(monthBox.selectionModel.selectedIndexProperty(), yearBox.valueProperty()) {
            YearMonth(yearBox.value, monthBox.selectionModel.selectedIndex + 1)
        })
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