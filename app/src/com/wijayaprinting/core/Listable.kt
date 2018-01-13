package com.wijayaprinting.core

import javafx.collections.ObservableList

interface Listable<T> {

    fun listAll(): ObservableList<T>
}