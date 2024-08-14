package com.hanggrian.openpss.control

import javafx.scene.layout.Region

open class Space
    @JvmOverloads
    constructor(width: Double = 0.0, height: Double = 0.0) :
    Region() {
        init {
            minWidth = width
            minHeight = height
        }
    }
