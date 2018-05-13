package pl.robertsreberski.ketchup.repos

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toFlowable
import pl.robertsreberski.ketchup.pojos.Interval
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.pojos.TimeEntry
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author: Robert Sreberski
 * Creation time: 23:06 11/05/2018
 * Package name: pl.robertsreberski.ketchup.repos
 */
@Singleton
class TimeEntriesRepository @Inject constructor() {

    var _entries = listOf<TimeEntry>()
    var _projects = listOf(Project(UUID.randomUUID().toString(), "Project 1", "#D81B60"), Project(UUID.randomUUID().toString(), "Project 2", "#E65100"))
    var _activeProject: Project? = null

    private fun getPreferredPomodoroLength(): Long {
        return 25 * 60 * 1000
    }

    private fun getPreferredBreakLength(): Long {
        return 5 * 60 * 1000
    }

    private fun getPomodoroNumber(): Int {
        return _entries.count { entry -> entry.type == TimeEntry.Type.POMODORO && entry.finished } + 1
    }

    fun setActiveProject(project: Project): Single<Boolean> {
        return Single.create {
            _activeProject = project

            _entries = _entries.map {
                if (it.type == TimeEntry.Type.POMODORO && !it.finished) {
                    it.project = project
                }
                it
            }

            it.onSuccess(true)
        }
    }

    fun startPomodoroEntry(): Single<TimeEntry> {
        return Single.create {
            Utils().finishCurrentPauseIfRunning()

            val notFinishedIndex = Utils().fetchIndexOfLastNotFinished()

            val pomodoro = if (notFinishedIndex > -1) _entries[notFinishedIndex] else
                TimeEntry(
                        type = TimeEntry.Type.POMODORO,
                        pomodoroNumber = getPomodoroNumber(),
                        plannedDuration = getPreferredPomodoroLength(),
                        project = _activeProject
                )

            pomodoro.intervals.add(Interval())

            _entries = if (notFinishedIndex > -1) _entries.mapIndexed { index, timeEntry ->
                if (index == notFinishedIndex) pomodoro
                else timeEntry
            } else _entries.plus(pomodoro)

            it.onSuccess(pomodoro)
        }
    }

    fun stopCurrentEntry(setFinished: Boolean): Single<Boolean> {
        return Single.create {
            _entries = _entries.map {
                if (it.finished) it
                else {
                    if (it.intervals.last().end == 0L)
                        it.intervals[it.intervals.size - 1].end = System.currentTimeMillis()

                    it.finished = setFinished
                    it
                }
            }

            it.onSuccess(true)
        }
    }

    fun pauseCurrentEntry(): Single<TimeEntry> {
        return Single.create { parent ->
            this.stopCurrentEntry(false).subscribeBy {
                val pause = TimeEntry(
                        type = TimeEntry.Type.PAUSE
                )

                pause.intervals.add(Interval())
                _entries = _entries.plus(pause)

                parent.onSuccess(pause)
            }
        }
    }

    fun abandonCurrentEntry(): Single<Boolean> {
        return Single.create {
            val originalSize = _entries.size
            val count = this.Utils().getNumberOfEntriesAfterLatestPomodoroIncluding()
            _entries = _entries.dropLast(count)

            it.onSuccess(_entries.size < originalSize)
        }
    }

    fun startBreakEntry(): Single<TimeEntry> {
        return Single.create {
            val breakEntry = TimeEntry(
                    type = TimeEntry.Type.BREAK,
                    plannedDuration = getPreferredBreakLength()
            )
            breakEntry.intervals.add(Interval())

            _entries = _entries.plus(breakEntry)
            it.onSuccess(breakEntry)
        }
    }

    val todaysPomodoros: Flowable<TimeEntry>
        get() = _entries.toFlowable().filter { it.type == TimeEntry.Type.POMODORO }
    val userProjects: Flowable<Project>
        get() = _projects.toFlowable()

    private inner class Utils {
        fun getNumberOfEntriesAfterLatestPomodoroIncluding(): Int {
            return _entries.size - _entries.indexOfLast { it.type == TimeEntry.Type.POMODORO }
        }

        fun finishCurrentPauseIfRunning() {
            if (_entries.isEmpty() || _entries.last().type != TimeEntry.Type.PAUSE) return

            val pause = _entries.last()
            pause.intervals[pause.intervals.size - 1].end = System.currentTimeMillis()
            pause.finished = true

            _entries = _entries.dropLast(1).plus(pause)
        }

        fun fetchIndexOfLastNotFinished(): Int {
            return _entries.indexOfLast { it.type == TimeEntry.Type.POMODORO && !it.finished }
        }
    }
}