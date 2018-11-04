package com.hendraanggrian.openpss.control

/** Defines a popup component that expects result to be returned. */
interface ResultablePopup<T> {

    /**
     * @return result of the component.
     */
    val nullableResult: T? get() = null
}