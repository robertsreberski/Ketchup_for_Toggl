package pl.robertsreberski.ketchup.scenes.main.components

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_timer.*
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.pojos.TimeEntry
import pl.robertsreberski.ketchup.pojos.Timer
import pl.robertsreberski.ketchup.reusables.PomodoroItem
import pl.robertsreberski.ketchup.scenes.main.MainViewModel
import pl.robertsreberski.ketchup.ui.ConnectedFragment
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class TimerFragment : ConnectedFragment<MainViewModel>() {

    val _hourFormatter = SimpleDateFormat("HH:mm")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_timer, container, false)

        return root
    }


    override fun connectViewToData() {
        bindObserver(viewModel.elapsedTime, Timer.NOT_SET, this::timeUpdater)
        bindObserver(viewModel.start, Timer.NOT_SET, this::startHourUpdater)
        bindObserver(viewModel.estimatedEnd, Timer.NOT_SET, this::endHourUpdater)
        bindObserver(viewModel.state, TimeEntry.Type.INACTIVE, this::stateUpdater)

        bindObserver(viewModel.remaining, Timer.NOT_SET, this::remainingUpdater)
        bindObserver(viewModel.todaysEntries, listOf(), this::entriesListUpdater)
    }

    private fun startHourUpdater(start: Long) {
        if (start == Timer.NOT_SET) {
            estimationLayout.visibility = View.GONE
            return
        }

        estimationLayout.visibility = View.VISIBLE
        startHourText.text = _hourFormatter.format(Date(start))
    }

    private fun endHourUpdater(end: Long) {
        endHourText.text = if (end != Timer.NOT_SET) _hourFormatter.format(Date(end)) else "?"
    }

    private fun remainingUpdater(remaining: Long) {
        if (remaining == Timer.NOT_SET) {
            remainingText.visibility = View.GONE
            return
        }

        remainingText.visibility = View.VISIBLE
        remainingText.text = "(%d mins)".format(TimeUnit.MILLISECONDS.toMinutes(remaining))
    }

    private fun stateUpdater(state: TimeEntry.Type) {
        stateText.text = state.toString()
    }

    private fun entriesListUpdater(pomodoros: List<TimeEntry>) {
        pomodoroList.removeAllViews()
        if (pomodoros.isEmpty()) return

        pomodoros.forEach {
            pomodoroList.addView(PomodoroItem(context!!, it.project ?: Project(), !it.finished))
        }
    }

    private fun timeUpdater(elapsedTime: Long) {
        timerMinutes.text = String.format("%02d", elapsedTime / 1000 / 60)
        timerSeconds.text = String.format("%02d", elapsedTime / 1000 % 60)
        timerMillis.text = String.format("%03d", elapsedTime % 1000)
    }


    override fun setViewModel(provider: ViewModelProvider): MainViewModel {
        return provider.get(MainViewModel::class.java)
    }
}
