package pl.robertsreberski.ketchup.di

import dagger.Module
import dagger.Provides
import pl.robertsreberski.ketchup.local.IntervalsLocal
import pl.robertsreberski.ketchup.local.ProjectsLocal
import pl.robertsreberski.ketchup.local.TimeEntriesLocal
import pl.robertsreberski.ketchup.repos.TimeEntriesRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Author: Robert Sreberski
 * Creation time: 21:25 11/05/2018
 * Package name: pl.robertsreberski.ketchup.di
 */
@Module(includes = [(ViewModelModule::class)])
class AppModule {
    @Provides
    fun provideExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    fun providesIntervalsLocal(): IntervalsLocal {
        return IntervalsLocal()
    }

    @Provides
    fun providesProjectsLocal(): ProjectsLocal {
        return ProjectsLocal()
    }

    @Provides
    fun providesTimeEntriesLocal(): TimeEntriesLocal {
        return TimeEntriesLocal()
    }

    @Provides
    fun provideTimeEntriesRepository(intervalsLocal: IntervalsLocal, projectsLocal: ProjectsLocal, timeEntriesLocal: TimeEntriesLocal): TimeEntriesRepository {
        return TimeEntriesRepository(intervalsLocal, timeEntriesLocal, projectsLocal)
    }


}