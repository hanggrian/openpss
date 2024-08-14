package com.hanggrian.openpss.ui.wage

import org.joda.time.Interval
import org.joda.time.ReadableInstant
import org.joda.time.ReadableInterval
import java.io.Serializable

/**
 * An [Interval] wrapper where start time may be bigger than end time, making the time difference
 * value negative. Such behavior is currently unsupported with [Interval] constructor.
 */
class SafeInterval private constructor(
    private val isReverse: Boolean,
    private val interval: Interval,
) : ReadableInterval by interval,
    Serializable by interval {
    val minutes: Int
        get() {
            var minutes = interval.toDuration().toStandardMinutes().minutes
            if (isReverse) {
                minutes *= -1
            }
            return minutes
        }

    inline val hours: Double get() = minutes / 60.0

    fun overlap(other: Interval): Interval? = interval.overlap(other)

    companion object {
        fun of(start: ReadableInstant, end: ReadableInstant): SafeInterval =
            (start > end).let { isReverse ->
                return SafeInterval(
                    isReverse,
                    when (isReverse) {
                        true -> Interval(end, start)
                        else -> Interval(start, end)
                    },
                )
            }
    }
}
