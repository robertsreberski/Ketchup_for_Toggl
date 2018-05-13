package pl.robertsreberski.ketchup.scenes.main.components

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_project.view.*
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.pojos.Project

/**
 * Author: Robert Sreberski
 * Creation time: 21:23 13/05/2018
 * Package name: pl.robertsreberski.ketchup.scenes.main.components
 */
class ProjectListAdapter constructor(
        private val context: Context,
        var data: List<Project>,
        val onProjectSelected: (Project) -> Unit
) : RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val root = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false)

        return ProjectViewHolder(root)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = data[position]

        holder.name = project.name
        holder.color = Color.parseColor(project.color)
    }

    fun setProjects(projects: List<Project>) {
        data = projects
        notifyDataSetChanged()
    }

    inner class ProjectViewHolder constructor(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var color: Int? = null
            set(value) {
                itemView.projectColor.setCardBackgroundColor(value ?: Color.WHITE)
                field = value
            }

        var name: String? = null
            set(value) {
                itemView.projectName.text = value ?: "Default project"
                field = value
            }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onProjectSelected(data[adapterPosition])
        }
    }
}