package pl.robertsreberski.ketchup.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import pl.robertsreberski.ketchup.factories.CustomViewModelFactory
import pl.robertsreberski.ketchup.scenes.main.MainViewModel

/**
 * Author: Robert Sreberski
 * Creation time: 21:10 11/05/2018
 * Package name: pl.robertsreberski.ketchup.di
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: CustomViewModelFactory): ViewModelProvider.Factory
}