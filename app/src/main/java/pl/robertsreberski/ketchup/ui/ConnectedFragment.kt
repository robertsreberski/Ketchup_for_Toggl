package pl.robertsreberski.ketchup.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.github.ajalt.timberkt.Timber
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Author: Robert Sreberski
 * Creation time: 09:29 12/05/2018
 * Package name: pl.robertsreberski.ketchup.scenes.main.components
 */
abstract class ConnectedFragment<T> : Fragment() {

    protected val viewModel: T
        get() = _viewModel!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var _viewModel: T? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AndroidSupportInjection.inject(this)

        if (!includeViewModel(activity)) {
            Timber.e { "Couldn't fetch parent activity -> Won't connect View to Data" }
            return
        }

        connectViewToData()
    }

    protected abstract fun connectViewToData()
    protected abstract fun setViewModel(provider: ViewModelProvider): T

    private fun includeViewModel(activity: FragmentActivity?): Boolean {
        if (activity != null) {
            _viewModel = setViewModel(ViewModelProviders.of(activity, viewModelFactory))
            return true
        }

        return false
    }

    protected fun <T> bindObserver(data: LiveData<T>, initial: T, callback: (T) -> Unit) {
        callback(data.value ?: initial)
        data.observe(this, Observer { value -> callback(value ?: initial) })
    }
}