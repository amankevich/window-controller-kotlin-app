package by.squareroot.windowcontroller.ui


import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import by.squareroot.windowcontroller.R
import by.squareroot.windowcontroller.api.WindowControllerAlarms
import by.squareroot.windowcontroller.api.WindowControllerDirection
import by.squareroot.windowcontroller.api.WindowControllerPosition
import by.squareroot.windowcontroller.data.WindowBlind
import by.squareroot.windowcontroller.util.WindowTimeUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_windowblind_control.*
import javax.inject.Inject

class WindowBlindControlNodeMCUFragment : WindowControlFragment() {
    @Inject
    lateinit var timeUtil: WindowTimeUtil
    private var alarms: WindowControllerAlarms? = null
    private var setAlarmsSubject = PublishSubject.create<Pair<WindowControllerDirection, Long>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manual_panel.visibility = View.GONE
        alarm_panel.visibility = View.VISIBLE

        disposables.add(
            setAlarmsSubject
                .observeOn(Schedulers.io())
                .flatMap {
                    val (direction, time) = it
                    viewModel.activeWindowBlind.value?.let { windowBlind ->
                        viewModel.setAlarm(windowBlind, direction, time)
                            .andThen(Observable.just(it))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val (direction, time) = it
                    Log.d(TAG, "alarm set for $direction, will trigger at $time")
                    invalidateAlarms(direction, time)
                    Toast.makeText(requireContext(), "Alarm was set", Toast.LENGTH_SHORT).show()
                }, {
                    Log.w(TAG, "can't set the alarm", it)
                    Toast.makeText(requireContext(), "Error setting alarm", Toast.LENGTH_SHORT).show()
                })
        )

        val getAlarmsSubject = PublishSubject.create<WindowBlind>()
        disposables.add(
            getAlarmsSubject
                .observeOn(Schedulers.io())
                .flatMap {
                    viewModel.getAlarms(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        onAlarmsReceivedFromWindow(it)
                    },
                    {
                        Log.w(TAG, "Can't get list of alarms")
                    })
        )

        viewModel.activeWindowBlind.observe(this, Observer {
            getAlarmsSubject.onNext(it)
        })

        set_alarm_up.setOnClickListener {
            showTimePickerDialog(WindowControllerDirection.UP)
        }

        set_alarm_down.setOnClickListener {
            showTimePickerDialog(WindowControllerDirection.DOWN)
        }
    }

    private fun onAlarmsReceivedFromWindow(alarms: WindowControllerAlarms) {
        alarms.down = alarms.down * 1000
        alarms.up = alarms.up * 1000
        invalidateAlarms(alarms)
    }

    private fun showTimePickerDialog(alarmDirection: WindowControllerDirection) {
        val (hour, minute) = alarms?.let {
            when (alarmDirection) {
                WindowControllerDirection.UP -> Pair(timeUtil.getHours24(it.up), timeUtil.getMinute(it.up))
                WindowControllerDirection.DOWN -> Pair(timeUtil.getHours24(it.down), timeUtil.getMinute(it.down))
            }
        } ?: Pair(timeUtil.currentHour24(), timeUtil.currentMinute())

        TimePickerDialog(
            requireContext(),
            R.style.DialogTheme,
            { _, hourOfDay, minutes ->
                setAlarmsSubject.onNext(Pair(alarmDirection, timeUtil.getTimeFromHour24Minute(hourOfDay, minutes)))
            },
            hour, minute, true
        ).show()
    }

    private fun invalidateAlarms(alarmDirection: WindowControllerDirection, time: Long) {
        val updatedAlarms: WindowControllerAlarms = alarms ?: WindowControllerAlarms("", -1, -1)
        when (alarmDirection) {
            WindowControllerDirection.UP -> updatedAlarms.up = time * 1000
            WindowControllerDirection.DOWN -> updatedAlarms.down = time * 1000
        }
        invalidateAlarms(updatedAlarms)
    }

    private fun invalidateAlarms(alarms: WindowControllerAlarms) {
        this.alarms = alarms
        if (alarms.down > 0) {
            alarm_down_time.text = timeUtil.toHourMinutesString(alarms.down)
        } else {
            alarm_down_time.text = "not set"
        }
        if (alarms.up > 0) {
            alarm_up_time.text = timeUtil.toHourMinutesString(alarms.up)
        } else {
            alarm_up_time.text = "not set"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.control_node_mcu_to_settings)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings, menu)
    }

    companion object {
        const val TAG = "WindowControlNodeMCU"
    }
}