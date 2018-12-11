package by.squareroot.windowcontroller.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.api.WindowControllerDirection
import by.squareroot.windowcontroller.api.WindowControllerPosition
import by.squareroot.windowcontroller.api.WindowControllerSide
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModel
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_windowblind_control.*
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import javax.inject.Inject


open class WindowControlFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: WindowBlindViewModelFactory
    protected lateinit var navController: NavController
    protected val disposables = CompositeDisposable()
    protected var connectionIsAlive = false
    protected val viewModel: WindowBlindViewModel by lazy {
        ViewModelProviders.of(requireActivity(), viewModelFactory).get(WindowBlindViewModel::class.java)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_windowblind_control, container, false)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun invalidateConnection(controllerConnected: Boolean) {
        connectionIsAlive = controllerConnected
        if (controllerConnected) {
            connected.setTextColor(Color.GREEN)
            connected.setText(R.string.fragment_control_connected)
        } else {
            connected.setTextColor(Color.RED)
            connected.setText(R.string.fragment_control_not_connected)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val checkConnectionSubject = PublishSubject.create<WindowBlind>()
        disposables.add(
            checkConnectionSubject
                .observeOn(Schedulers.io())
                .flatMap {
                    viewModel.checkConnection(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { window ->
                        invalidateConnection(window.connected)
                    },
                    {
                        invalidateConnection(false)
                    })
        )

        viewModel.activeWindowBlind.observe(this, Observer {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it.name
            checkConnectionSubject.onNext(it)
        })

        auto_up.setOnClickListener {
            onClicked(it)
        }

        auto_down.setOnClickListener {
            onClicked(it)
        }

        stop_button.setOnClickListener {
            onClicked(it)
        }

        subscribePeriodicalPositionUpdates()
    }

    private fun onClicked(view: View) {
        if (connectionIsAlive) {
            viewModel.activeWindowBlind.value?.let { windowBlind ->
                when (view.id) {
                    R.id.stop_button -> viewModel.stopWindowBlind(windowBlind)
                    R.id.auto_down -> viewModel.downWindowBlind(windowBlind)
                    R.id.auto_up -> viewModel.upWindowBlind(windowBlind)
                    else -> Completable.error(IllegalArgumentException("view with id ${view.id} not registered"))
                }
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        Log.d(TAG, "window controller command completed")
                    }, {
                        Log.w(TAG, "error during window controller command occured", it)
                    })
            }
        }
    }

    private fun subscribePeriodicalPositionUpdates() {
        disposables.add(
            Observable.interval(1, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap {
                    if (connectionIsAlive) {
                        viewModel.activeWindowBlind.value?.let {
                            viewModel.getPosition(it)
                        } ?: run {
                            Observable.empty<WindowControllerPosition>()
                        }
                    } else {
                        Observable.empty<WindowControllerPosition>()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "window blinds position, left =  ${it.left} %, right = ${it.right} %")
                    left_pos.text = "${it.left} %"
                    right_pos.text = "${it.right} %"
                    invalidateConnection(true)
                }, {
                    Log.w(TAG, "can't retrieve window position", it)
                })
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.control, menu)
    }

    private fun delete() {
        val viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(WindowBlindViewModel::class.java)
        viewModel.activeWindowBlind.value?.let {
            viewModel.delete(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
        navController.navigateUp()
    }

    private fun edit() {
        navController.navigate(R.id.control_to_edit)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_delete -> {
                delete()
                true
            }

            R.id.action_edit -> {
                edit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "WindowControl"
    }
}