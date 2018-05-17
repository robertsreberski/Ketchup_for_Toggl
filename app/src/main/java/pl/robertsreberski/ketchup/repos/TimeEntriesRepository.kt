package pl.robertsreberski.ketchup.repos

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import pl.robertsreberski.ketchup.local.IntervalsLocal
import pl.robertsreberski.ketchup.local.ProjectsLocal
import pl.robertsreberski.ketchup.local.TimeEntriesLocal
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
class TimeEntriesRepository @Inject constructor(
        val intervalsLocal: IntervalsLocal,
        val timeEntriesLocal: TimeEntriesLocal,
        val projectsLocal: ProjectsLocal
) {

    var _activeProjectPublisher = PublishSubject.create<Project>()
    var _activeProject: Project = Project()
        set(value) {
            _activeProjectPublisher.onNext(value)
            field = value
        }

    private fun getPreferredPomodoroLength(): Long {
        return 25 * 60 * 1000
    }

    private fun getPreferredBreakLength(): Long {
        return 5 * 60 * 1000
    }

    private fun getPomodoroNumber(): Int {
        return timeEntriesLocal.getLatestQuery(TimeEntry.Type.POMODORO, true)?.let {
            it.pomodoroNumber + 1
        } ?: 1
    }

    fun setActiveProject(project: Project = Project()): Single<Boolean> {
        return Single.create<Boolean> {
            _activeProject = project

            val pomodoro = timeEntriesLocal.getLatestQuery(TimeEntry.Type.POMODORO, false)
            if (pomodoro != null) {
                pomodoro.project = project
                timeEntriesLocal.save(pomodoro)
            }

            it.onSuccess(true)
        }
    }

    fun fetchCurrentEntry(): Maybe<TimeEntry> {
        return Maybe.create<TimeEntry> {
            val entry = timeEntriesLocal.getLatestQuery(null, false)

            if (entry != null) it.onSuccess(entry)
            else it.onComplete()
        }
    }

    fun startPomodoroEntry(): Single<TimeEntry> {
        return Single.create {
            Utils().finishCurrentPauseIfRunning()

            val pomodoro = timeEntriesLocal.getLatestQuery(TimeEntry.Type.POMODORO, false)
                    ?: TimeEntry(
                            type = TimeEntry.Type.POMODORO.name,
                            pomodoroNumber = getPomodoroNumber(),
                            plannedDuration = getPreferredPomodoroLength(),
                            project = _activeProject
                    )

            pomodoro.intervals.add(intervalsLocal.createDefault())
            timeEntriesLocal.save(pomodoro)

            it.onSuccess(pomodoro)
        }
    }


    fun stopCurrentEntry(setFinished: Boolean): Single<Boolean> {
        return Single.create {

            var unfinished = timeEntriesLocal.listNotFinishedEntries()
            unfinished = unfinished.map {
                it.finished = setFinished
                if (it.intervals.last()?.end == 0L) {
                    val interval = it.intervals.last()!!
                    intervalsLocal.finishInterval(interval)
                }
                it
            }

            timeEntriesLocal.saveAll(unfinished)
            it.onSuccess(true)
        }
    }

    fun pauseCurrentEntry(): Single<TimeEntry> {
        return Single.create { parent ->
            this.stopCurrentEntry(false).subscribeBy {
                val pause = TimeEntry(
                        type = TimeEntry.Type.PAUSE.name
                )

                pause.intervals.add(intervalsLocal.createDefault())
                timeEntriesLocal.save(pause)

                parent.onSuccess(pause)
            }
        }
    }

    fun abandonCurrentEntry(): Single<Boolean> {
        return Single.create {
            val pomodoro = timeEntriesLocal.getLatestQuery(TimeEntry.Type.POMODORO, false)
            pomodoro?.let { timeEntriesLocal.removeAllAfter(it, true) }

            it.onSuccess(true)
        }
    }

    fun startBreakEntry(): Single<TimeEntry> {
        return Single.create { parent ->
            this.stopCurrentEntry(true).subscribeBy {
                val breakEntry = TimeEntry(
                        type = TimeEntry.Type.BREAK.name,
                        plannedDuration = getPreferredBreakLength()
                )
                breakEntry.intervals.add(intervalsLocal.createDefault())

                timeEntriesLocal.save(breakEntry)
                parent.onSuccess(breakEntry)
            }
        }
    }

    val activeProject: Flowable<Project>
        get() = _activeProjectPublisher.toFlowable(BackpressureStrategy.LATEST)
    val todaysPomodoros: Flowable<List<TimeEntry>> = timeEntriesLocal.getPomodorosFlowableForToday(
            Utils().getCurrentDateInMillis())
    val userProjects: Flowable<List<Project>> = projectsLocal.getAllProjects()
    var currentEntry: Flowable<List<TimeEntry>> = timeEntriesLocal.getCurrentEntry()

    private inner class Utils {

        fun getCurrentDateInMillis(): Long {
            val calendar = GregorianCalendar()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            return calendar.timeInMillis
        }

        fun finishCurrentPauseIfRunning() {
            var pause = timeEntriesLocal.getLatestQuery()
            if (pause == null || pause.getConvertedType() != TimeEntry.Type.PAUSE) return

            pause.intervals.add(intervalsLocal.createDefault())
            pause.finished = true

            timeEntriesLocal.save(pause)
        }

    }
}