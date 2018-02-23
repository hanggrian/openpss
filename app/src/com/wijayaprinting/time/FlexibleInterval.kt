package com.wijayaprinting.time

import org.joda.time.Interval
import org.joda.time.ReadableInstant

/**
 * An [Interval] wrapper where start time may be bigger than end time, making the time difference value negative.
 * Such behavior is currently unsupported with [Interval] constructor.
 */
class FlexibleInterval private constructor(private val isReverse: Boolean, val interval: Interval) {

    constructor(start: ReadableInstant, end: ReadableInstant) : this(start > end, when (start > end) {
        true -> Interval(end, start)
        else -> Interval(start, end)
    })

    val minutes: Int
        get() {
            var minutes = interval.toDuration().toStandardMinutes().minutes
            if (isReverse) minutes *= -1
            return minutes
        }

    inline val hours: Double get() = minutes / 60.0

    fun overlap(other: Interval): Interval? = interval.overlap(other)
}