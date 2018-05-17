package pl.robertsreberski.ketchup.scenes.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.pojos.TimeEntry
import pl.robertsreberski.ketchup.pojos.TimeEntry.Type.*
import pl.robertsreberski.ketchup.pojos.Timer
import pl.robertsreberski.ketchup.repos.TimeEntriesRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject

/**
 * Author: Robert Sreberski
 * Creation time: 23:21 10/05/2018
 * Package name: pl.robertsreberski.ketchup.scenes.main
 */
class MainViewModel @Inject constructor(val timeRepo: TimeEntriesRepository) : ViewModel() {

    @Inject
    lateinit var _executorService: ExecutorService

    private var _currentTimerTask: Future<*>? = null

    private var _todaysEntries: MutableLiveData<List<TimeEntry>> = MutableLiveData()
    private var _availableProjects: MutableLiveData<List<Project>> = MutableLiveData()
    private var _activeProject: MutableLiveData<Project> = MutableLiveData()
    private var _shouldShowOptions: MutableLiveData<Boolean> = MutableLiveData()
    private var _shouldShowProjectList: MutableLiveData<Boolean> = MutableLiveData()
    private var _timer: Timer = Timer()


    fun startTimer() {
        this.handleAsynchronous(timeRepo.startPomodoroEntry()) {
            stopTimerTask()
            applyEntryToTimer(it)
            startTimerTask()
        }
    }

    fun pauseTimer() {
        this.handleAsynchronous(timeRepo.pauseCurrentEntry()) {
            stopTimerTask()
            applyEntryToTimer(it)
            startTimerTask()
        }
    }

    fun stopTimer() {
        toggleOptionsView()
        finishTimeEntry()
    }

    fun restartTimer() {
        toggleOptionsView()
        this.handleAsynchronous(timeRepo.abandonCurrentEntry()) {
            if (it) startTimer()
        }
    }

    fun abandonTimer() {
        toggleOptionsView()
        finishTimeEntry(true)
    }

    fun toggleOptionsView(show: Boolean = !(_shouldShowOptions.value ?: false)) {
        _shouldShowOptions.postValue(show)
    }

    fun toggleProjectListView(show: Boolean = !(_shouldShowProjectList.value ?: false)) {
        _shouldShowProjectList.postValue(show)
    }

    fun selectActiveProject(project: Project) {
        timeRepo.setActiveProject(project).subscribeBy {
            if (it) {
                _activeProject.postValue(project)
                toggleProjectListView(false)
            }
        }
    }

    private fun finishTimeEntry(abandon: Boolean = false) {
        this.handleAsynchronous(
                if (abandon) timeRepo.abandonCurrentEntry()
                else timeRepo.stopCurrentEntry(true)
        ) {
            if (it) {
                stopTimerTask()
                applyEntryToTimer(TimeEntry(type = INACTIVE.name))
            }
        }
    }

    private fun startBreakEntry() {
        this.handleAsynchronous(timeRepo.startBreakEntry()) {
            stopTimerTask()
            applyEntryToTimer(it)
            startTimerTask()
        }
    }

    private fun startTimerTask() {
        _currentTimerTask = _executorService.submit(TimerTask())
    }

    private fun stopTimerTask() {
        if (_currentTimerTask != null) _currentTimerTask!!.cancel(true)
    }

    private fun applyEntryToTimer(entry: TimeEntry) {
        _timer._state = entry.getConvertedType()

        _timer._start = entry.getStartTime()
        _timer._elapsed = entry.getElapsedTime()
        _timer._plannedDuration = entry.plannedDuration

        _timer._estimatedEnd = if (_timer._state == POMODORO || _timer.state == BREAK)
            entry.getEstimatedEnd() else TimeEntry.NO_ESTIMATED_END
    }

    fun attachSubscribers() {
        timeRepo.todaysPomodoros.subscribe {
            this._todaysEntries.postValue(it)
        }
        timeRepo.userProjects.subscribe {
            this._availableProjects.postValue(it)
        }
        timeRepo.currentEntry.subscribe {
            if (it.isEmpty()) {
                stopTimerTask()
                applyEntryToTimer(TimeEntry(type = INACTIVE.name))
            } else {
                stopTimerTask()
                applyEntryToTimer(it.first())
                startTimerTask()
            }
        }

        timeRepo.activeProject.subscribe {
            if (it.isEmpty()) _activeProject.postValue(Project())
            else _activeProject.postValue(it.first())
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> handleAsynchronous(task: Single<T>, callback: (T) -> Unit) {
        (task as Single<*>).subscribeOn(Schedulers.trampoline()).subscribeBy {
            callback(it as T)
        }
    }

    val elapsedTime: LiveData<Long>
        get() = _timer.elapsed
    val state: LiveData<TimeEntry.Type>
        get() = _timer.state
    val estimatedEnd: LiveData<Long>
        get() = _timer.estimatedEnd
    val start: LiveData<Long>
        get() = _timer.start
    val remaining: LiveData<Long>
        get() = _timer.remaining
    val todaysEntries: LiveData<List<TimeEntry>>
        get() = _todaysEntries
    val shouldShowOptions: LiveData<Boolean>
        get() = _shouldShowOptions
    val availableProjects: LiveData<List<Project>>
        get() = _availableProjects
    val activeProject: LiveData<Project>
        get() = _activeProject
    val shouldShowProjectList: LiveData<Boolean>
        get() = _shouldShowProjectList

    inner class TimerTask : Runnable {

        private var _elapsed: Long = _timer._elapsed
        private val _limit: Long = _timer._plannedDuration


        override fun run() {
            var timestamp: Long = System.currentTimeMillis()

            while (true) {
                Thread.sleep(5)
                this._elapsed += System.currentTimeMillis() - timestamp
                timestamp = System.currentTimeMillis()

                _timer._elapsed = this._elapsed

                if (isEntryLimited() && _limit < this._elapsed) break
                else {
                    _timer._remaining = _limit - this._elapsed
                }
            }

            if (_timer._state == POMODORO) startBreakEntry()
            else finishTimeEntry()
        }

        private fun isEntryLimited() = _limit > 0

    }
}