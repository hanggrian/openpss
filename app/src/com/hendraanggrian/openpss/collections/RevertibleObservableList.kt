package com.hendraanggrian.openpss.collections

import javafx.collections.ObservableList
import kfx.collections.mutableObservableListOf
import kfx.collections.toMutableObservableList

class RevertibleObservableList<E>(
    private val actual: ObservableList<E> = mutableObservableListOf()
) : ObservableList<E> by actual {

    private val clone: ObservableList<E> = actual.toMutableObservableList()

    fun revert() {
        actual.clear()
        actual.addAll(clone)
    }

    fun addRevertable(element: E): Boolean {
        clone.add(element)
        return actual.add(element)
    }

    fun addRevertable(index: Int, element: E) {
        clone.add(index, element)
        actual.add(index, element)
    }

    fun addAllRevertable(index: Int, elements: Collection<E>): Boolean {
        clone.addAll(index, elements)
        return actual.addAll(index, elements)
    }

    fun addAllRevertable(elements: Collection<E>): Boolean {
        clone.addAll(elements)
        return actual.addAll(elements)
    }

    fun clearRevertable() {
        clone.clear()
        actual.clear()
    }

    fun removeRevertable(element: E): Boolean {
        clone.remove(element)
        return actual.remove(element)
    }

    fun removeAllRevertable(elements: Collection<E>): Boolean {
        clone.removeAll(elements)
        return actual.removeAll(elements)
    }

    fun removeAtRevertable(index: Int): E {
        clone.removeAt(index)
        return actual.removeAt(index)
    }

    fun removeAllRevertable(vararg elements: E): Boolean {
        clone.removeAll(*elements)
        return actual.removeAll(*elements)
    }

    fun setRevertable(index: Int, element: E): E {
        clone[index] = element
        return actual.set(index, element)
    }

    fun addAllRevertable(vararg elements: E): Boolean {
        clone.addAll(*elements)
        return actual.addAll(*elements)
    }

    fun removeRevertable(from: Int, to: Int) {
        clone.remove(from, to)
        actual.remove(from, to)
    }

    fun setAllRevertable(vararg elements: E): Boolean {
        clone.setAll(*elements)
        return actual.setAll(*elements)
    }

    fun setAllRevertable(col: MutableCollection<out E>?): Boolean {
        clone.setAll(col)
        return actual.setAll(col)
    }

    fun retainAllRevertable(elements: Collection<E>): Boolean {
        clone.retainAll(elements)
        return actual.retainAll(elements)
    }

    fun retainAllRevertable(vararg elements: E): Boolean {
        clone.retainAll(*elements)
        return actual.retainAll(*elements)
    }
}