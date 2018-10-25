package com.hendraanggrian.openpss

import com.hendraanggrian.openpss.db.schemas.Employee
import com.hendraanggrian.openpss.i18n.Resourced
import javafx.scene.layout.StackPane

interface Context : Resourced {

    val employee: Employee

    val dialogContainer: StackPane
}