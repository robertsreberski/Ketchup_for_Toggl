package pl.robertsreberski.ketchup.scenes.main.components

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_options.*
import kotlinx.android.synthetic.main.fragment_options.view.*
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.pojos.TimeEntry
import pl.robertsreberski.ketchup.pojos.TimeEntry.Type.*
import pl.robertsreberski.ketchup.scenes.main.MainViewModel
import pl.robertsreberski.ketchup.ui.ConnectedFragment

/**
 * Author: Robert Sreberski
 * Creation time: 23:12 10/05/2018
 * Package name: pl.robertsreberski.ketchup.scenes.main
 */
class OptionsFragment : ConnectedFragment<MainViewModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_options, container, false)

        root.startButton.setOnClickListener { viewModel.startTimer() }
        root.pauseButton.setOnClickListener { viewModel.pauseTimer() }
        root.stopButton.setOnClickListener { viewModel.stopTimer() }
        root.restartButton.setOnClickListener { viewModel.restartTimer() }
        root.deleteButton.setOnClickListener { viewModel.abandonTimer() }
        root.optionsMenuButton.setOnClickListener { viewModel.toggleOptionsView() }
        root.selectProjectButton.setOnClickListener(this::onSelectProjectButtonClicked)

        return root
    }

    override fun connectViewToData() {
        bindObserver(viewModel.shouldShowOptions, false, this::changeOptionsVisibility)
        bindObserver(viewModel.state, INACTIVE, this::prepareOptionsForState)
    }


    private fun changeOptionsVisibility(show: Boolean) {
        mainOptionsCard.visibility = if (show) View.GONE else View.VISIBLE
        optionsContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun prepareOptionsForState(state: TimeEntry.Type) {
        mainOptionsCard.visibility = View.VISIBLE
        when (state) {
            INACTIVE -> {
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
                optionsPanelCard.visibility = View.GONE
            }
            POMODORO -> {
                startButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
                optionsPanelCard.visibility = View.VISIBLE
            }
            BREAK -> {
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
                optionsPanelCard.visibility = View.GONE
            }
            PAUSE -> {
                startButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
                optionsPanelCard.visibility = View.VISIBLE
            }
        }
    }

    private fun onSelectProjectButtonClicked(v: View) {

    }

    override fun setViewModel(provider: ViewModelProvider): MainViewModel {
        return provider.get(MainViewModel::class.java)
    }
}