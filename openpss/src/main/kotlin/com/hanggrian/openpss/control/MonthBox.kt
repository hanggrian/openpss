package com.hanggrian.openpss.control

import com.hanggrian.openpss.R
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.image.ImageView
import ktfx.bindings.bindingOf
import ktfx.collections.toObservableList
import ktfx.controls.CENTER
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxComboBox
import ktfx.layouts.KtfxHBox
import ktfx.text.buildStringConverter
import org.joda.time.YearMonth
import org.joda.time.YearMonth.now
import java.text.DateFormatSymbols.getInstance
import java.util.Locale

open class MonthBox
    @JvmOverloads
    constructor(prefill: YearMonth = now()) : KtfxHBox(0.0) {
        val monthBox: ComboBox<Int>
        val yearField: IntField
        val previousButton: Button
        val nextButton: Button

        val valueProperty: ObjectProperty<YearMonth> = SimpleObjectProperty()
        val value: YearMonth? by valueProperty

        private var months = getInstance().shortMonths

        init {
            alignment = CENTER
            previousButton = jfxButton(graphic = ImageView(R.image_btn_previous))
            monthBox =
                jfxComboBox((0 until 12).toObservableList()) {
                    value = prefill.monthOfYear - 1
                    converter =
                        buildStringConverter {
                            toString { months[it!!] }
                            fromString { months.indexOf(it) }
                        }
                }
            yearField =
                addChild(
                    IntField().apply {
                        alignment = CENTER
                        maxWidth = 56.0
                        value = prefill.year
                    },
                )
            nextButton =
                jfxButton(graphic = ImageView(R.image_btn_next)) {
                    onAction {
                        monthBox.value =
                            when (monthBox.value) {
                                11 -> {
                                    yearField.value += 1
                                    0
                                }
                                else -> monthBox.value + 1
                            }
                    }
                }

            previousButton.onAction {
                monthBox.value =
                    when (monthBox.value) {
                        0 -> {
                            yearField.value -= 1
                            11
                        }
                        else -> monthBox.value - 1
                    }
            }

            valueProperty.bind(
                bindingOf(
                    monthBox.selectionModel.selectedIndexProperty(),
                    yearField.valueProperty,
                ) {
                    YearMonth(yearField.value, monthBox.value + 1)
                },
            )
        }

        fun setLocale(locale: Locale) {
            months = getInstance(locale).shortMonths
            monthBox.converter =
                buildStringConverter {
                    fromString { months.indexOf(it) }
                    toString { months[it!!] }
                }
        }
    }
