package pl.robertsreberski.ketchup.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import pl.robertsreberski.ketchup.App
import javax.inject.Singleton

/**
 * Author: Robert Sreberski
 * Creation time: 21:12 11/05/2018
 * Package name: pl.robertsreberski.ketchup.di
 */
@Singleton
@Component(modules = [(ActivityModule::class), (FragmentModule::class), (AppModule::class)])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}
