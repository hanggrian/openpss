package com.hendraanggrian.openpss.popup

/** Defines a popup component that expects result to be returned. */
interface ResultablePopup<T> {

    /**
     * @return result of the component.
     */
    val nullableResult: T? get() = null
}