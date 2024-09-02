package com.hanggrian.openpss.control

import com.hanggrian.openpss.R
import com.hanggrian.openpss.util.toJava
import com.hanggrian.openpss.util.toJoda
import com.jfoenix.controls.JFXTimePicker
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import ktfx.bindings.bindingOf
import ktfx.controls.CENTER
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxTimePicker
import ktfx.jfoenix.layouts.styledJfxButton
import ktfx.layouts.KtfxHBox
import ktfx.text.buildStringConverter
import org.joda.time.LocalTime
import org.joda.time.LocalTime.MIDNIGHT

/**
 * Two fields (hour and minute) that represents [LocalTime].
 *
 * [TimeBox] width is deliberately measured to match [com.hanggrian.ui.scene.control.ForcedDatePicker]'s width.
 */
open class TimeBox
    @JvmOverloads
    constructor(prefill: LocalTime = MIDNIGHT) :
    KtfxHBox(0.0) {
        val picker: JFXTimePicker
        val previousButton: Button
        val nextButton: Button
        var onOverlap: ((Boolean) -> Unit)? = null

        val valueProperty: ObjectProperty<LocalTime> = SimpleObjectProperty()
        val value: LocalTime? by valueProperty

        init {
            alignment = CENTER
            previousButton = styledJfxButton(null, ImageView(R.image_btn_previous), R.style_flat)
            picker =
                jfxTimePicker {
                    editor.alignment = CENTER
                    is24HourView = true
                    value = prefill.toJava()
                    isEditable = false
                    maxWidth = 116.0
                    converter =
                        buildStringConverter {
                            fromString {
                                val a = it.split(':')
                                java.time.LocalTime.of(a[0].toInt(), a[1].toInt(), 0)
                            }
                            toString {
                                val s = it.toString()
                                when {
                                    s.split(':').size > 2 -> s.substringBeforeLast(':')
                                    else -> s
                                }
                            }
                        }
                }
            nextButton =
                styledJfxButton(null, ImageView(R.image_btn_next), R.style_flat) {
                    onAction {
                        picker.value =
                            when (picker.value.hour) {
                                23 -> {
                                    onOverlap?.invoke(true)
                                    java.time.LocalTime
                                        .of(0, picker.value.minute, picker.value.second)
                                }
                                else -> picker.value.plusHours(1)
                            }
                    }
                }

            previousButton.onAction {
                picker.value =
                    when (picker.value.hour) {
                        0 -> {
                            onOverlap?.invoke(false)
                            java.time.LocalTime
                                .of(23, picker.value.minute, picker.value.second)
                        }
                        else -> picker.value.minusHours(1)
                    }
            }

            valueProperty.bind(bindingOf(picker.valueProperty()) { picker.value.toJoda() })
        }
    }
