package pl.robertsreberski.ketchup.scenes.main.components

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_account.view.*
import pl.robertsreberski.ketchup.R
import pl.robertsreberski.ketchup.scenes.main.MainViewModel
import pl.robertsreberski.ketchup.ui.ConnectedFragment

/**
 * Author: Robert Sreberski
 * Creation time: 23:11 10/05/2018
 * Package name: pl.robertsreberski.ketchup.scenes.main
 */
class AccountFragment : ConnectedFragment<ViewModel>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_account, container, false)

        root.syncButton.setOnClickListener(this::onSyncButtonClicked)
        root.logOutButton.setOnClickListener(this::onLogOutButtonClicked)

        return root
    }

    override fun connectViewToData() {

    }

    private fun onSyncButtonClicked(v: View) {

    }

    private fun onLogOutButtonClicked(v: View) {

    }

    override fun setViewModel(provider: ViewModelProvider): ViewModel {
        return provider.get(MainViewModel::class.java)
    }
}