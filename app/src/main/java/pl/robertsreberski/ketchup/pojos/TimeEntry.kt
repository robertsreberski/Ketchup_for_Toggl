package pl.robertsreberski.ketchup.pojos

/**
 * Author: Robert Sreberski
 * Creation time: 19:14 11/05/2018
 * Package name: pl.robertsreberski.ketchup.pojos
 */
data class TimeEntry(
        var project: Project? = null,
        var intervals: MutableList<Interval> = mutableListOf(),
        var type: Type = Type.POMODORO,
        var pomodoroNumber: Int = 1,
        var plannedDuration: Long = 0,
        var finished: Boolean = false) {

    companion object {
        const val NO_ESTIMATED_END: Long = 0
    }

    enum class Type { POMODORO, PAUSE, BREAK, INACTIVE }

    fun getElapsedTime(): Long {
        return intervals.sumByLong {
            if (it.end > 0L) it.end - it.start else it.end
        }
    }

    fun getActualStartTime() = intervals.last().start
    fun getEstimatedEnd() = getActualStartTime() + getRemainingTime()
    fun getRemainingTime() = plannedDuration - getElapsedTime()

    inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
        var sum = 0L
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }
}
