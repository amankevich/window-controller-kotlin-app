package by.squareroot.windowcontroller.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.consts.VERSION_NODE_MCU
import by.squareroot.windowcontroller.consts.VERSION_ORANGE_PI
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_new_window_blind.*

class WindowBlindEditFragment : WindowBlindDetailsFragment() {
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_window_blind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        new_window_blind_create.setText(R.string.fragment_edit_button)

        val viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(WindowBlindViewModel::class.java)
        viewModel.activeWindowBlind.observe(this, Observer {
                edit_address.setText(it.address)
                edit_name.setText(it.name)
                when (it.apiVersion) {
                    VERSION_ORANGE_PI -> spinner_api_version.setSelection(0)
                    VERSION_NODE_MCU -> spinner_api_version.setSelection(1)
                }
            }
        )

        disposables.add(windowBlindFromInputSubject
            .observeOn(Schedulers.io())
            .flatMap {
                viewModel.activeWindowBlind.value?.let { active ->
                    it.id = active.id
                }
                viewModel.activeWindowBlind.postValue(it)
                viewModel.update(it).toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navController.navigateUp()
            })
    }
}