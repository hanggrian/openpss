package com.wijayaprinting.ui

import javafx.collections.ObservableList

interface Listable<T> {

    fun listAll(): ObservableList<T>
}