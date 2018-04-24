package com.hendraanggrian.openpss.resources

import javafx.collections.ObservableList

interface Listable<T> {

    fun listAll(resourced: Resourced): ObservableList<T>
}