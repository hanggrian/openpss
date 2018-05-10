@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.controls

import com.hendraanggrian.openpss.util.stretchableText
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.SplitMenuButton
import javafx.scene.image.ImageView
import ktfx.beans.value.getValue
import ktfx.beans.value.setValue
import ktfx.coroutines.listener

class StretchableSplitMenuButton @JvmOverloads constructor(
    stretchableText: String? = null,
    graphicUrl: String? = null
) : SplitMenuButton() {

    private var stretchableTextProperty = SimpleStringProperty()
    fun stretchableTextProperty(): StringProperty = stretchableTextProperty
    var stretchableText: String? by stretchableTextProperty

    private var graphicUrlProperty = SimpleStringProperty()
    fun graphicUrlProperty(): StringProperty = graphicUrlProperty
    var graphicUrl: String? by graphicUrlProperty

    init {
        this.stretchableTextProperty.listener { _, _, value -> stretchableText(value) }
        this.stretchableText = stretchableText
        this.graphicUrlProperty.listener { _, _, value -> graphic = value?.let { ImageView(it) } }
        this.graphicUrl = graphicUrl
    }
}