package by.squareroot.windowcontroller.api

import android.util.Log
import io.reactivex.Observable
import java.lang.IllegalArgumentException

class WindowControllerNodeMCUAdapter(private val controller: WindowControllerNodeMCU) : WindowController {
    override fun setSetting(
        direction: WindowControllerDirection,
        side: WindowControllerSide,
        time: Int
    ): Observable<Boolean> {
        return controller.setSetting(side.value, direction.value, time).map {
            Log.d(TAG, "set up down timer response: $it")
            it.status == NODE_MCU_RESPONSE_SUCCESS
        }
    }

    override fun getSettings(): Observable<WindowControllerSettings> {
        return controller.getSettings()
    }

    override fun setAlarm(direction: WindowControllerDirection, time: Long): Observable<Boolean> {
        return controller.scheduleAlarm(direction.value, time).map {
            Log.d(TAG, "alarm scheduled response: $it")
            it.status == NODE_MCU_RESPONSE_SUCCESS
        }
    }

    override fun getAlarms(): Observable<WindowControllerAlarms> {
        return controller.getScheduledAlarms()
    }

    override fun autoMove(direction: WindowControllerDirection): Observable<Boolean> {
        return when (direction) {
            WindowControllerDirection.UP -> controller.up()
            WindowControllerDirection.DOWN -> controller.down()
        }.map {
            Log.d(TAG, "auto move response: $it")
            it.status == NODE_MCU_RESPONSE_SUCCESS
        }
    }

    override fun manualMove(
        direction: WindowControllerDirection,
        side: WindowControllerSide,
        time: Int
    ): Observable<Boolean> {
        return Observable.error(IllegalArgumentException("WindowControllerNodeMCUAdapter doesn't support manual control"))
    }

    override fun stop(): Observable<Boolean> {
        return controller.stop()
            .map {
                Log.d(TAG, "stop response: ${it.status}")
                it.status.isNotEmpty()
            }
    }

    override fun position(): Observable<WindowControllerPosition> {
        return controller.getStatus()
            .map {
                if (NODE_MCU_POSITION_DOWN == it.position) {
                    WindowControllerPosition(100, 100)
                } else {
                    WindowControllerPosition(0, 0)
                }
            }
    }

    companion object {
        const val TAG = "ControllerNodeMCU"
    }
}