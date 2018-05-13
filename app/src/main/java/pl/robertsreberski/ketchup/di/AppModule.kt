package pl.robertsreberski.ketchup.di

import dagger.Module
import dagger.Provides
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
    fun provideTimeEntriesRepository(): TimeEntriesRepository {
        return TimeEntriesRepository()
    }
}