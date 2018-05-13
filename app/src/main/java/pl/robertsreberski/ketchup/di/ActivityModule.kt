package pl.robertsreberski.ketchup.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.robertsreberski.ketchup.scenes.main.components.MainActivity

/**
 * Author: Robert Sreberski
 * Creation time: 21:04 11/05/2018
 * Package name: pl.robertsreberski.ketchup.di
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [(FragmentModule::class)])
    abstract fun contributeMainActivity(): MainActivity
}