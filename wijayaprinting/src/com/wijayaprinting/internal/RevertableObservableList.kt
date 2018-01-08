package com.wijayaprinting.internal

import javafx.collections.ObservableList
import kotfx.mutableObservableListOf
import kotfx.toMutableObservableList

class RevertableObservableList<E> @JvmOverloads constructor(private val actual: ObservableList<E> = mutableObservableListOf()) : ObservableList<E> by actual {

    private val duplicate: ObservableList<E> = actual.toMutableObservableList()

    fun revert() {
        actual.clear()
        actual.addAll(duplicate)
    }

    fun addRevertable(element: E): Boolean {
        duplicate.add(element)
        return actual.add(element)
    }

    fun addRevertable(index: Int, element: E) {
        duplicate.add(index, element)
        actual.add(index, element)
    }

    fun addAllRevertable(index: Int, elements: Collection<E>): Boolean {
        duplicate.addAll(index, elements)
        return actual.addAll(index, elements)
    }

    fun addAllRevertable(elements: Collection<E>): Boolean {
        duplicate.addAll(elements)
        return actual.addAll(elements)
    }

    fun clearRevertable() {
        duplicate.clear()
        actual.clear()
    }

    fun removeRevertable(element: E): Boolean {
        duplicate.remove(element)
        return actual.remove(element)
    }

    fun removeAllRevertable(elements: Collection<E>): Boolean {
        duplicate.removeAll(elements)
        return actual.removeAll(elements)
    }

    fun removeAtRevertable(index: Int): E {
        duplicate.removeAt(index)
        return actual.removeAt(index)
    }

    fun removeAllRevertable(vararg elements: E): Boolean {
        duplicate.removeAll(*elements)
        return actual.removeAll(*elements)
    }

    fun setRevertable(index: Int, element: E): E {
        duplicate[index] = element
        return actual.set(index, element)
    }

    fun addAllRevertable(vararg elements: E): Boolean {
        duplicate.addAll(*elements)
        return actual.addAll(*elements)
    }

    fun removeRevertable(from: Int, to: Int) {
        duplicate.remove(from, to)
        actual.remove(from, to)
    }

    fun setAllRevertable(vararg elements: E): Boolean {
        duplicate.setAll(*elements)
        return actual.setAll(*elements)
    }

    fun setAllRevertable(col: MutableCollection<out E>?): Boolean {
        duplicate.setAll(col)
        return actual.setAll(col)
    }

    fun retainAllRevertable(elements: Collection<E>): Boolean {
        duplicate.retainAll(elements)
        return actual.retainAll(elements)
    }

    fun retainAllRevertable(vararg elements: E): Boolean {
        duplicate.retainAll(*elements)
        return actual.retainAll(*elements)
    }
}