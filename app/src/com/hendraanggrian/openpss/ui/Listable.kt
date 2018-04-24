package com.hendraanggrian.openpss.ui

import javafx.collections.ObservableList

interface Listable<T> {

    fun listAll(resourced: Resourced): ObservableList<T>
}