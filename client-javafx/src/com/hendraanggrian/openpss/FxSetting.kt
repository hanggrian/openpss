package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.ui.wage.EClockingReader
import java.io.File
import java.util.Properties

class FxSetting : Setting<FxSetting.Editor> {

    companion object {
        const val KEY_WAGEREADER = "wage_reader"
    }

    private val file = File(MainDirectory, ".settings")
    private val properties = Properties()

    init {
        if (!file.exists()) {
            file.createNewFile()
        }
        file.inputStream().use { properties.load(it) }
    }

    override fun contains(key: String): Boolean = properties.containsKey(key)

    override fun getString(key: String): String = properties.getProperty(key)

    override fun getEditor(): Editor = Editor()

    override fun setDefault(editor: Editor) {
        super.setDefault(editor)
        if (KEY_WAGEREADER !in this) {
            editor[KEY_WAGEREADER] = EClockingReader.name
        }
    }

    override fun Editor.set(key: String, value: String) = putString(key, value)

    override fun Editor.save() {
        file.outputStream().use { properties.store(it, null) }
    }

    inner class Editor {

        fun putString(key: String, value: String) {
            properties.setProperty(key, value)
        }
    }
}