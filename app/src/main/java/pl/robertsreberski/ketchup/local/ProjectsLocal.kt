package pl.robertsreberski.ketchup.local


import com.vicpin.krealmextensions.*
import io.reactivex.Flowable
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.pojos.ProjectFields
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author: Robert Sreberski
 * Creation time: 12:46 15/05/2018
 * Package name: pl.robertsreberski.ketchup.db
 */
@Singleton
class ProjectsLocal @Inject constructor() {

    fun getAllProjectsFlowable(): Flowable<List<Project>> {
        return Project().queryAllAsFlowable()
    }

    fun getAllProjects(): List<Project> {
        return Project().queryAll()
    }

    fun saveAll(projects: List<Project>) {
        projects.saveAll()
    }

    fun getActiveProject(): Project? {
        val found = query<Project> { equalTo(ProjectFields.ACTIVE, true) }
        return if (found.isEmpty()) null else found.first()
    }

    fun getActiveProjectFlowable(): Flowable<List<Project>> {
        return queryAsFlowable {
            equalTo(ProjectFields.ACTIVE, true)
        }
    }
}