package com.wijayaprinting.base

import javafx.collections.ObservableList

interface Listable<T> {

    fun listAll(): ObservableList<T>
}