package by.squareroot.windowcontroller.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.api.WindowControllerDirection
import by.squareroot.windowcontroller.api.WindowControllerSettings
import by.squareroot.windowcontroller.api.WindowControllerSide
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModel
import by.squareroot.windowcontroller.ui.viewmodel.WindowBlindViewModelFactory
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_windowblind_settings.*
import javax.inject.Inject

class WindowControllerSettingsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: WindowBlindViewModelFactory
    private val disposables = CompositeDisposable()
    private val viewModel: WindowBlindViewModel by lazy {
        ViewModelProviders.of(requireActivity(), viewModelFactory).get(WindowBlindViewModel::class.java)
    }
    private var timeValue: Int = DEFAULT_TIME_VALUE
    private val getSettingsSubject = PublishSubject.create<WindowBlind>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_windowblind_settings, container, false)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposables.add(
            getSettingsSubject
                .observeOn(Schedulers.io())
                .flatMap {
                    viewModel.getSettings(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::invalidateSettings, ::logError)
        )

        viewModel.activeWindowBlind.observe(this, Observer {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = it.name
            getSettingsSubject.onNext(it)
        })

        time_value_minus.setOnClickListener {
            changeTimeValue(-STEP_TIME)
        }

        time_value_plus.setOnClickListener {
            changeTimeValue(STEP_TIME)
        }

        set_left_up_timer.setOnClickListener {
            setSetting(WindowControllerSide.LEFT, WindowControllerDirection.UP)
        }
        set_left_down_timer.setOnClickListener {
            setSetting(WindowControllerSide.LEFT, WindowControllerDirection.DOWN)
        }
        set_right_up_timer.setOnClickListener {
            setSetting(WindowControllerSide.RIGHT, WindowControllerDirection.UP)
        }
        set_right_down_timer.setOnClickListener {
            setSetting(WindowControllerSide.RIGHT, WindowControllerDirection.DOWN)
        }
    }

    private fun setSetting(side: WindowControllerSide, direction: WindowControllerDirection) {
        viewModel.activeWindowBlind.value?.let {
            viewModel.setSetting(it, side, direction, timeValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "setting was saved")
                    Toast.makeText(requireContext(), "Time setting was saved", Toast.LENGTH_SHORT).show()
                    getSettingsSubject.onNext(it)
                }, {
                    Log.w(TAG, "setting was not saved", it)
                    Toast.makeText(requireContext(), "Error saving time setting", Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun changeTimeValue(value: Int) {
        if (timeValue > 0) {
            timeValue += value
            time_value_text.text = "$timeValue ms"
        }
    }

    private fun invalidateSettings(settings: WindowControllerSettings) {
        timer_left_up_text.text = "${settings.upLeft} ms"
        timer_left_down_text.text = "${settings.downLeft} ms"
        timer_right_up_text.text = "${settings.upRight} ms"
        timer_right_down_text.text = "${settings.downRight} ms"
        timeValue = maxOf(maxOf(settings.upLeft, settings.downLeft), maxOf(settings.upRight, settings.downRight))
        changeTimeValue(0)
    }

    private fun logError(ex: Throwable) {
        Log.w(TAG, "Can't get list of alarms")
    }

    companion object {
        const val TAG = "WindowControllerSetting"
        const val DEFAULT_TIME_VALUE = 1000
        const val STEP_TIME = 100
    }
}