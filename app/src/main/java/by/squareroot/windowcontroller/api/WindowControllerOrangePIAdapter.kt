package by.squareroot.windowcontroller.api

import android.util.Log
import io.reactivex.Observable
import java.lang.IllegalArgumentException

class WindowControllerOrangePIAdapter(private val controller: WindowControllerOrangePI) : WindowController {
    override fun setSetting(
        direction: WindowControllerDirection,
        side: WindowControllerSide,
        time: Int
    ): Observable<Boolean> {
        return Observable.error(IllegalArgumentException("WindowControllerOrangePIAdapter doesn't support remote settings"))
    }

    override fun getSettings(): Observable<WindowControllerSettings> {
        return Observable.error(IllegalArgumentException("WindowControllerOrangePIAdapter doesn't support remote settings"))
    }

    override fun setAlarm(direction: WindowControllerDirection, time: Long): Observable<Boolean> {
        return Observable.error(IllegalArgumentException("WindowControllerOrangePIAdapter doesn't support alarms"))
    }

    override fun getAlarms(): Observable<WindowControllerAlarms> {
        return Observable.error(IllegalArgumentException("WindowControllerOrangePIAdapter doesn't support alarms"))
    }

    override fun manualMove(
        direction: WindowControllerDirection,
        side: WindowControllerSide,
        time: Int
    ): Observable<Boolean> {
        return controller.move(side.value, direction.value, time)
            .map {
                Log.d(TAG, "manual move response: $it")
                it.isNotEmpty()
            }
    }

    override fun stop(): Observable<Boolean> {
        return controller.stop()
            .map {
                Log.d(TAG, "stop response: $it")
                it.isNotEmpty()
            }
    }

    override fun autoMove(direction: WindowControllerDirection): Observable<Boolean> {
        return when (direction) {
            WindowControllerDirection.UP -> controller.up()
            WindowControllerDirection.DOWN -> controller.down()
        }.map {
            Log.d(TAG, "auto move response: $it")
            it.isNotEmpty()
        }
    }

    override fun position(): Observable<WindowControllerPosition> {
        return controller.getStatus()
            .map {
                WindowControllerPosition(it.left, it.right)
            }
    }

    companion object {
        const val TAG = "ControllerOrangePI"
    }
}