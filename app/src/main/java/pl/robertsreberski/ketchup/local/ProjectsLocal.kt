package pl.robertsreberski.ketchup.local


import com.vicpin.krealmextensions.queryAllAsFlowable
import io.reactivex.Flowable
import pl.robertsreberski.ketchup.pojos.Project
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author: Robert Sreberski
 * Creation time: 12:46 15/05/2018
 * Package name: pl.robertsreberski.ketchup.db
 */
@Singleton
class ProjectsLocal @Inject constructor() {

    fun getAllProjects(): Flowable<List<Project>> {
        return Project().queryAllAsFlowable()
    }
}