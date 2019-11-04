package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.FxComponent
import com.hendraanggrian.openpss.R
import com.hendraanggrian.openpss.R2
import com.hendraanggrian.openpss.control.DateBox
import com.hendraanggrian.openpss.control.TimeBox
import com.hendraanggrian.openpss.ui.wage.record.Record
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import kotlinx.coroutines.CoroutineScope
import ktfx.bindings.buildBinding
import ktfx.bindings.isBlank
import ktfx.controls.gap
import ktfx.coroutines.onAction
import ktfx.getValue
import ktfx.jfoenix.layouts.jfxButton
import ktfx.jfoenix.layouts.jfxTextField
import ktfx.layouts.NodeManager
import ktfx.layouts.addNode
import ktfx.layouts.gridPane
import ktfx.setValue
import org.controlsfx.control.PopOver
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime

/** Base popup class used across applications. */
@Suppress("LeakingThis")
open class BasePopOver(
    component: FxComponent,
    override val titleId: String
) : PopOver(), BasePopup, FxComponent by component {

    override fun setActualContent(region: Region) {
        contentNode = region
    }

    override fun setOnShown(onShown: () -> Unit) = super.setOnShown { onShown() }

    override fun dismiss() = hide()

    override lateinit var contentPane: VBox
    override lateinit var buttonManager: NodeManager
    override lateinit var cancelButton: Button

    private val graphicProperty = SimpleObjectProperty<Node>()
    override fun graphicProperty(): ObjectProperty<Node> = graphicProperty
    var graphic: Node? by graphicProperty

    init {
        initialize()
    }
}

/** [PopOver] with default button and return type. */
open class ResultablePopOver<T>(
    component: FxComponent,
    titleId: String
) : BasePopOver(component, titleId), ResultablePopup<T> {

    override lateinit var defaultButton: Button

    fun show(node: Node, onAction: suspend CoroutineScope.(T?) -> Unit) {
        show(node)
        defaultButton.onAction {
            onAction(nullableResult!!)
            hide()
        }
    }
}

open class InputPopOver(component: FxComponent, titleId: String, prefill: String? = null) :
    ResultablePopOver<String>(component, titleId) {

    protected val editor: TextField = jfxTextField(prefill)

    open val defaultDisableBinding: BooleanBinding get() = editor.textProperty().isBlank()

    override val focusedNode: Node? get() = editor

    init {
        defaultButton.run {
            text = getString(R2.string.ok)
            disableProperty().bind(defaultDisableBinding)
            editor.onActionProperty()
                .bind(buildBinding(disableProperty()) { if (isDisable) null else onAction })
        }
    }

    override val nullableResult: String? get() = editor.text
}

class DatePopOver(
    component: FxComponent,
    titleId: String,
    prefill: LocalDate = LocalDate.now()
) : ResultablePopOver<LocalDate>(component, titleId) {

    private val dateBox: DateBox = DateBox(prefill)

    override val nullableResult: LocalDate? get() = dateBox.valueProperty().value
}

class TimePopOver(
    component: FxComponent,
    titleId: String,
    prefill: LocalTime = LocalTime.now()
) : ResultablePopOver<LocalTime>(component, titleId) {

    private val timeBox: TimeBox = TimeBox(prefill)

    override val nullableResult: LocalTime? get() = timeBox.valueProperty().value
}

class DateTimePopOver(
    component: FxComponent,
    titleId: String,
    defaultButtonTextId: String,
    prefill: DateTime
) : ResultablePopOver<DateTime>(component, titleId) {

    private val dateBox: DateBox
    private lateinit var timeBox: TimeBox

    init {
        gridPane {
            gap = getDouble(R.value.padding_medium)
            dateBox = addNode(DateBox(prefill.toLocalDate())) row 0 col 1
            jfxButton("-${Record.WORKING_HOURS}") {
                onAction {
                    repeat(Record.WORKING_HOURS) {
                        timeBox.previousButton.fire()
                    }
                }
            } row 1 col 0
            timeBox = addNode(TimeBox(prefill.toLocalTime())) {
                onOverlap = { plus ->
                    dateBox.picker.value = when {
                        plus -> dateBox.picker.value.plusDays(1)
                        else -> dateBox.picker.value.minusDays(1)
                    }
                }
            } row 1 col 1
            jfxButton("+${Record.WORKING_HOURS}") {
                onAction {
                    repeat(Record.WORKING_HOURS) {
                        timeBox.nextButton.fire()
                    }
                }
            } row 1 col 2
        }
        defaultButton.text = getString(defaultButtonTextId)
    }

    override val nullableResult: DateTime? get() = dateBox.value!!.toDateTime(timeBox.value)
}
