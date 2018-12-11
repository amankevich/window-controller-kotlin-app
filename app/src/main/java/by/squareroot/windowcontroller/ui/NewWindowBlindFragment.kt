package by.squareroot.windowcontroller.ui


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.ui.viewmodel.NewWindowViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewWindowBlindFragment : WindowBlindDetailsFragment() {
    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_window_blind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(NewWindowViewModel::class.java)
        disposables.add(windowBlindFromInputSubject
            .observeOn(Schedulers.io())
            .flatMap {
                viewModel.createNewWindow(it).toObservable()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                navController.navigateUp()
            })
    }
}
