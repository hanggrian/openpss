package com.hendraanggrian.openpss.collections

import javafx.collections.ObservableList
import ktfx.collections.mutableObservableListOf
import ktfx.collections.toMutableObservableList

class RevertibleObservableList<E>(
    private val actual: ObservableList<E> = mutableObservableListOf()
) : ObservableList<E> by actual {

    private val clone: ObservableList<E> = actual.toMutableObservableList()

    fun revert() {
        actual.clear()
        actual += clone
    }

    fun addRevertible(element: E): Boolean {
        clone += element
        return actual.add(element)
    }

    fun addRevertible(index: Int, element: E) {
        clone.add(index, element)
        actual.add(index, element)
    }

    fun addAllRevertible(index: Int, elements: Collection<E>): Boolean {
        clone.addAll(index, elements)
        return actual.addAll(index, elements)
    }

    fun addAllRevertible(elements: Collection<E>): Boolean {
        clone += elements
        return actual.addAll(elements)
    }

    fun clearRevertible() {
        clone.clear()
        actual.clear()
    }

    fun removeRevertible(element: E): Boolean {
        clone -= element
        return actual.remove(element)
    }

    fun removeAllRevertible(elements: Collection<E>): Boolean {
        clone -= elements
        return actual.removeAll(elements)
    }

    fun removeAtRevertible(index: Int): E {
        clone.removeAt(index)
        return actual.removeAt(index)
    }

    fun removeAllRevertible(vararg elements: E): Boolean {
        clone -= elements
        return actual.removeAll(*elements)
    }

    fun setRevertible(index: Int, element: E): E {
        clone[index] = element
        return actual.set(index, element)
    }

    fun addAllRevertible(vararg elements: E): Boolean {
        clone += elements
        return actual.addAll(*elements)
    }

    fun removeRevertible(from: Int, to: Int) {
        clone.remove(from, to)
        actual.remove(from, to)
    }

    fun setAllRevertible(vararg elements: E): Boolean {
        clone.setAll(*elements)
        return actual.setAll(*elements)
    }

    fun setAllRevertible(col: MutableCollection<out E>?): Boolean {
        clone.setAll(col)
        return actual.setAll(col)
    }

    fun retainAllRevertible(elements: Collection<E>): Boolean {
        clone.retainAll(elements)
        return actual.retainAll(elements)
    }

    fun retainAllRevertible(vararg elements: E): Boolean {
        clone.retainAll(*elements)
        return actual.retainAll(*elements)
    }
}