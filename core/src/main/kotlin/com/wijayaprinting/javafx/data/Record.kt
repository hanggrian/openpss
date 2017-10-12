package com.wijayaprinting.javafx.data

import com.wijayaprinting.mysql.utils.PATTERN_DATETIME
import com.wijayaprinting.mysql.utils.PATTERN_TIME
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import kotfx.bindings.doubleBindingOf
import kotfx.bindings.plus
import org.apache.commons.math3.util.Precision.round
import org.joda.time.DateTime

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
data class Record(
        val type: Int,
        private val mEmployee: Employee,
        private val mStart: DateTime,
        private val mEnd: DateTime,

        val daily: DoubleProperty = SimpleDoubleProperty(),
        val overtime: DoubleProperty = SimpleDoubleProperty(),

        val dailyIncome: DoubleProperty = SimpleDoubleProperty(),
        val overtimeIncome: DoubleProperty = SimpleDoubleProperty(),

        val total: DoubleProperty = SimpleDoubleProperty()
) {

    val employee: Employee?
        get() = when (type) {
            TYPE_NODE -> mEmployee
            TYPE_CHILD -> null
            TYPE_TOTAL -> null
            else -> throw UnsupportedOperationException()
        }

    val start: String
        get() = when (type) {
            TYPE_NODE -> mStart.toString(PATTERN_TIME)
            TYPE_CHILD -> mStart.toString(PATTERN_DATETIME)
            TYPE_TOTAL -> ""
            else -> throw UnsupportedOperationException()
        }

    val end: String
        get() = when (type) {
            TYPE_NODE -> mEnd.toString(PATTERN_TIME)
            TYPE_CHILD -> mEnd.toString(PATTERN_DATETIME)
            TYPE_TOTAL -> ""
            else -> throw UnsupportedOperationException()
        }

    init {
        if (type != TYPE_ROOT) {
            dailyIncome.bind(doubleBindingOf(daily, mEmployee.daily) { round(daily.value * mEmployee.daily.value /*/ mShift.workingHours*/, 2) })
            overtimeIncome.bind(doubleBindingOf(overtime, mEmployee.hourlyOvertime) { round(mEmployee.hourlyOvertime.value * overtime.value, 2) })
            when (type) {
                TYPE_NODE -> {
                    daily.set(0.0/*mShift.workingHours*/)
                    overtime.set(0.0)
                    total.set(0.0)
                }
                TYPE_CHILD -> {
                    /*val mShiftStart = mShift.getActualStart(mStart)
                    val mShiftEnd = mShift.getActualEnd(mEnd)
                    var mDaily: Double
                    val mHourlyOvertime: Double
                    when {
                    // comes late, ends early
                        mStart >= mShiftStart && mEnd <= mShiftEnd -> {
                            mDaily = mEnd.hoursDiff(mStart)
                            mHourlyOvertime = 0.0
                        }
                    // comes early, ends early
                        mStart < mShiftStart && mEnd <= mShiftEnd -> {
                            mDaily = mEnd.hoursDiff(mShiftStart)
                            mHourlyOvertime = mShiftStart.hoursDiff(mStart)
                        }
                    // comes late, ends late
                        mStart >= mShiftStart && mEnd > mShiftEnd -> {
                            mDaily = mShiftEnd.hoursDiff(mStart)
                            mHourlyOvertime = mEnd.hoursDiff(mShiftEnd)
                        }
                    // comes early, ends late, employee of the month!
                        mStart < mShiftStart && mEnd > mShiftEnd -> {
                            mDaily = mShiftEnd.hoursDiff(mShiftStart)
                            mHourlyOvertime = mShiftStart.hoursDiff(mStart) + mEnd.hoursDiff(mShiftEnd)
                        }
                        else -> error("Fatal calculation error!")
                    }
                    // recesses
                    mDaily -= mShift.recess.toDouble()
                    daily.set(if (mDaily < 0) 0.0 else round(mDaily, 2))
                    overtime.set(round(mHourlyOvertime, 2))
                    total.bind(dailyIncome + overtimeIncome)*/
                }
                TYPE_TOTAL -> total.bind(dailyIncome + overtimeIncome)
            }
        }
    }

    companion object {
        /** Dummy since [javafx.scene.control.TreeTableView] must have a root item. */
        const val TYPE_ROOT = 0
        /** Parent row displaying employee and its preferences. */
        const val TYPE_NODE = 1
        /** Child row of a node, displaying an actual record data. */
        const val TYPE_CHILD = 2
        /** Last child row of a node, displaying calculated total. */
        const val TYPE_TOTAL = 3

        private var ROOT: Record? = null

        val root: Record
            get() {
                if (ROOT == null) {
                    ROOT = Record(TYPE_ROOT, Employee(0, ""), DateTime(0), DateTime(0))
                }
                return ROOT!!
            }
    }
}