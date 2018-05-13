package pl.robertsreberski.ketchup.scenes.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.pojos.TimeEntry
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

    var _currentTimerTask: Future<*>? = null

    private var _todaysEntries: MutableLiveData<List<TimeEntry>> = MutableLiveData()
    private var _availableProjects: MutableLiveData<List<Project>> = MutableLiveData()
    private var _activeProject: MutableLiveData<Project> = MutableLiveData()
    private var _shouldShowOptions: MutableLiveData<Boolean> = MutableLiveData()
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

    fun toggleOptionsView() {
        _shouldShowOptions.postValue(!(_shouldShowOptions.value ?: false))
    }

    private fun finishTimeEntry(abandon: Boolean = false) {
        this.handleAsynchronous(
                if (abandon) timeRepo.abandonCurrentEntry()
                else timeRepo.stopCurrentEntry(true)
        ) {
            if (it) {
                stopTimerTask()

                _timer._state = TimeEntry.Type.INACTIVE
                _timer._remaining = Timer.NO_REMAINING_TIME
                _timer._start = Timer.NO_START_HOUR
                _timer._elapsed = 0
            }
        }
    }

    private fun startTimerTask() {
        _currentTimerTask = _executorService.submit(TimerTask())
    }

    private fun stopTimerTask() {
        if (_currentTimerTask != null) _currentTimerTask!!.cancel(true)
    }

    private fun applyEntryToTimer(entry: TimeEntry) {
        _timer._state = entry.type

        when (_timer._state) {
            TimeEntry.Type.POMODORO -> {
                _timer._start = entry.getActualStartTime()
                _timer._estimatedEnd = entry.getEstimatedEnd()
                _timer._elapsed = entry.getElapsedTime()
            }
            TimeEntry.Type.BREAK, TimeEntry.Type.PAUSE -> {
                _timer._start = entry.getActualStartTime()
                _timer._estimatedEnd = TimeEntry.NO_ESTIMATED_END
                _timer._elapsed = entry.getElapsedTime()
            }
            TimeEntry.Type.INACTIVE -> Timber.e { "There shouldn't be any conversion during inactive state!" }
        }
    }

    private fun synchronizePomodoroList() {
        timeRepo.todaysPomodoros.toList().subscribeBy {
            this._todaysEntries.postValue(it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> handleAsynchronous(task: Single<T>, callback: (T) -> Unit) {
        (task as Single<*>).subscribeOn(Schedulers.single()).subscribeBy {
            callback(it as T)
            this.synchronizePomodoroList()
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

    inner class TimerTask : Runnable {

        private var _elapsed: Long = _timer._elapsed
        private val _start: Long = _timer._start
        private val _end: Long = _timer._estimatedEnd
        private val _limit: Long = _end - _start


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

            finishTimeEntry()
        }

        private fun isEntryLimited() = _limit > 0

    }
}