package pl.robertsreberski.ketchup.scenes.main.components

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.factories.CustomViewModelFactory
import pl.robertsreberski.ketchup.pojos.Project
import pl.robertsreberski.ketchup.scenes.main.MainViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var viewModelFactory: CustomViewModelFactory

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidInjection.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            viewModel.activeProject.observe(this, Observer {
                setBackgroundForActiveProject(it ?: Project())
            })
            injectChildFragments()
        }

        viewModel.attachSubscribers()
    }

    private fun setBackgroundForActiveProject(project: Project) {
        mainParent.setBackgroundColor(Color.parseColor(project.color))
    }

    private fun injectChildFragments() {
        injectSingleFragment(timerFragment.id, TimerFragment())
        injectSingleFragment(optionsFragment.id, OptionsFragment())
        injectSingleFragment(accountFragment.id, AccountFragment())
    }

    private fun injectSingleFragment(id: Int, fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(id, fragment)
                .commit()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector
}
