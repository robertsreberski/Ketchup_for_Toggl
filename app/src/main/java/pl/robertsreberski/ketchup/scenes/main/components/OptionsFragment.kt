package pl.robertsreberski.ketchup.scenes.main.components

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_options.*
import kotlinx.android.synthetic.main.fragment_options.view.*
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.pojos.Project
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

    private lateinit var _projectListAdapter: ProjectListAdapter

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
        root.selectProjectButton.setOnClickListener { viewModel.toggleProjectListView() }
        configureProjectsList(root.projectList)

        return root
    }

    override fun connectViewToData() {
        bindObserver(viewModel.shouldShowOptions, false, this::changeOptionsVisibility)
        bindObserver(viewModel.state, INACTIVE, this::prepareOptionsForState)
        bindObserver(viewModel.availableProjects, listOf(), this::loadProjects)
        bindObserver(viewModel.shouldShowProjectList, false, this::changeProjectListVisibility)
        bindObserver(viewModel.activeProject, Project(), this::setProjectName)
    }

    private fun configureProjectsList(recyclerView: RecyclerView) {
        this._projectListAdapter = ProjectListAdapter(context!!, listOf()) {
            viewModel.selectActiveProject(it)
        }

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = this._projectListAdapter
    }

    private fun changeOptionsVisibility(show: Boolean) {
        mainOptionsCard.visibility = if (show) View.GONE else View.VISIBLE
        optionsContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun changeProjectListVisibility(show: Boolean) {
        projectListContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setProjectName(project: Project?) {
        currentProjectName.text = if (project != null) project.name else "(no project)"
    }

    private fun loadProjects(projects: List<Project>) {
        this._projectListAdapter.setProjects(projects)
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

    override fun setViewModel(provider: ViewModelProvider): MainViewModel {
        return provider.get(MainViewModel::class.java)
    }
}