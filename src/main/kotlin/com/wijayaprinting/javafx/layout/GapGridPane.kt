package com.wijayaprinting.javafx.layout

import javafx.scene.layout.GridPane

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
open class GapGridPane(hgap: Double, vgap: Double) : GridPane() {

    constructor(gap: Double) : this(gap, gap)

    init {
        this.hgap = hgap
        this.vgap = vgap
    }
}