package pl.robertsreberski.ketchup.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.robertsreberski.ketchup.scenes.main.components.AccountFragment
import pl.robertsreberski.ketchup.scenes.main.components.OptionsFragment
import pl.robertsreberski.ketchup.scenes.main.components.TimerFragment

/**
 * Author: Robert Sreberski
 * Creation time: 21:05 11/05/2018
 * Package name: pl.robertsreberski.ketchup.di
 */
@Module
public abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector
    abstract fun contributeTimerFragment(): TimerFragment

    @ContributesAndroidInjector
    abstract fun contributeOptionsFragment(): OptionsFragment
}
