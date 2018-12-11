package by.squareroot.windowcontroller.ui


import android.os.Bundle
import android.util.Log
import android.view.View
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.api.WindowControllerDirection
import by.squareroot.windowcontroller.api.WindowControllerSide
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_windowblind_control.*
import java.lang.IllegalArgumentException

class WindowBlindControlOrangePIFragment : WindowControlFragment() {
    private var manualTime = DEFAULT_MANUAL_TIME

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manual_panel.visibility = View.VISIBLE
        alarm_panel.visibility = View.GONE

        left_up_1_sec.setOnClickListener {
            onClicked(it)
        }
        left_down_1_sec.setOnClickListener {
            onClicked(it)
        }
        right_up_1_sec.setOnClickListener {
            onClicked(it)
        }
        right_down_1_sec.setOnClickListener {
            onClicked(it)
        }

        manual_time_minus.setOnClickListener {
            changeManualTime(-STEP_MANUAL_TIME)
        }

        manual_time_plus.setOnClickListener {
            changeManualTime(STEP_MANUAL_TIME)
        }
    }

    private fun changeManualTime(value: Int) {
        if (manualTime > 0) {
            manualTime = manualTime + value
            manual_time_value.text = "$manualTime ms"
        }
    }

    private fun onClicked(view: View) {
        if (connectionIsAlive) {
            viewModel.activeWindowBlind.value?.let { windowBlind ->
                when (view.id) {
                    R.id.left_up_1_sec -> viewModel.manualMove(
                        windowBlind,
                        WindowControllerDirection.UP,
                        WindowControllerSide.LEFT,
                        manualTime
                    )
                    R.id.left_down_1_sec -> viewModel.manualMove(
                        windowBlind,
                        WindowControllerDirection.DOWN,
                        WindowControllerSide.LEFT,
                        manualTime
                    )
                    R.id.right_up_1_sec -> viewModel.manualMove(
                        windowBlind,
                        WindowControllerDirection.UP,
                        WindowControllerSide.RIGHT,
                        manualTime
                    )
                    R.id.right_down_1_sec -> viewModel.manualMove(
                        windowBlind,
                        WindowControllerDirection.DOWN,
                        WindowControllerSide.RIGHT,
                        manualTime
                    )
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

    companion object {
        const val TAG = "WindowControlOrangePI"
        const val DEFAULT_MANUAL_TIME = 1000
        const val STEP_MANUAL_TIME = 100
    }
}