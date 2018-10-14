package com.hendraanggrian.openpss.lifecycle

import javafx.scene.Node
import ktfx.layouts.LayoutManager

interface Lifecylce {

    fun LayoutManager<Node>.onCreate() {
    }

    fun LayoutManager<Node>.onCreateActions() {
    }
}