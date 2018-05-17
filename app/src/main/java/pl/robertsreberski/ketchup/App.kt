package pl.robertsreberski.ketchup

import android.app.Activity
import android.app.Application
import android.content.Context
import com.github.ajalt.timberkt.Timber
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.realm.Realm
import pl.robertsreberski.ketchup.di.DaggerAppComponent
import javax.inject.Inject

/**
 * Author: Robert Sreberski
 * Creation time: 21:11 11/05/2018
 * Package name: pl.robertsreberski.ketchup
 */
class App : Application(), HasActivityInjector {

    lateinit var context: Context

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Realm.init(this)
        DaggerAppComponent.builder().application(this).build().inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

}