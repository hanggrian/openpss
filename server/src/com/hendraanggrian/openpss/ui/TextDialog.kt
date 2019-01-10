package com.hendraanggrian.openpss.ui

import com.hendraanggrian.openpss.BuildConfig
import com.hendraanggrian.openpss.StringResources
import java.awt.Button
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Label

class TextDialog(
    resources: StringResources,
    frame: Frame,
    textId: String
) : Dialog(frame), StringResources by resources {

    init {
        title = buildString {
            append("${BuildConfig.NAME} ${BuildConfig.VERSION}")
            if (BuildConfig.DEBUG) {
                append(" - DEBUG")
            }
        }
        setSize(256, 64)
        layout = FlowLayout(FlowLayout.CENTER)
        add(Label(getString(textId)))
        add(Button("OK").apply {
            addActionListener {
                this@TextDialog.isVisible = false
            }
        })
    }

    fun show2() {
        isVisible = true
    }
}