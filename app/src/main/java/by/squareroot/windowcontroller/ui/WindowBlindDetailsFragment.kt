package by.squareroot.windowcontroller.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.ui.viewmodel.NewWindowViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_new_window_blind.*
import javax.inject.Inject

open class WindowBlindDetailsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: NewWindowViewModelFactory
    lateinit var navController: NavController
    val windowBlindFromInputSubject = PublishSubject.create<WindowBlind>()
    val disposables = CompositeDisposable()

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_window_blind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.api_versions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_api_version.adapter = adapter
        }

        new_window_blind_create.setOnClickListener {
            val window = windowBlindFromInput()
            if (window != null) {
                windowBlindFromInputSubject.onNext(window)
            } else {
                Toast.makeText(requireContext(), R.string.fragment_new_window_blind_cant_create, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun windowBlindFromInput(): WindowBlind? {
        val address = edit_address.text.toString()
        if (address.isEmpty()) {
            return null
        }

        if (edit_name.text.isEmpty()) {
            return null
        }

        if (spinner_api_version.selectedItemPosition == AdapterView.INVALID_POSITION) {
            return null
        }

        return WindowBlind(0, edit_name.text.toString(), address, spinner_api_version.selectedItemPosition)
    }
}