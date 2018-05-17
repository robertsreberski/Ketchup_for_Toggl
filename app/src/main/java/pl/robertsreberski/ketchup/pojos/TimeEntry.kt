package pl.robertsreberski.ketchup.pojos

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Author: Robert Sreberski
 * Creation time: 12:01 15/05/2018
 * Package name: pl.robertsreberski.ketchup.pojos
 */
open class TimeEntry constructor(
        @PrimaryKey open var createdAt: Long = System.currentTimeMillis(),
        open var togglId: Long = -1,
        open var type: String = Type.POMODORO.name,
        open var project: Project? = null,
        open var pomodoroNumber: Int = 0,
        open var plannedDuration: Long = 0,
        open var intervals: RealmList<Interval> = RealmList(),
        open var finished: Boolean = false,
        open var exists: Boolean = true
) : RealmObject() {

    fun getConvertedType(): Type {
        return Type.valueOf(type)
    }

    fun getElapsedTime(): Long {
        return intervals.sumByLong {
            (if (it.end > 0L) it.end else System.currentTimeMillis()) - it.start
        }
    }

    fun getStartTime() = if (intervals.isNotEmpty()) intervals.first()!!.start else 0
    fun getEstimatedEnd() = getStartTime().plus(getRemainingTime())
    fun getRemainingTime() = plannedDuration - getElapsedTime()

    inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
        var sum = 0L
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }

    enum class Type { POMODORO, PAUSE, BREAK, INACTIVE }

    companion object {
        const val NO_ESTIMATED_END: Long = 0
    }
}